package request;

import bll.AuthenticationManager;
import bll.exceptions.AccessDeniedException;
import bll.exceptions.BllException;
import bll.exceptions.InvalidCredentialsException;
import bll.exceptions.LoggedInException;
import bll.exceptions.ServerConnectionException;
import bll.observer.Channel;
import bll.observer.ChannelObservable;
import bll.service.GenericService;
import bll.service.UserService;
import lombok.Getter;
import request.controllers.Controller;
import request.security.RequestAccess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
This is the Front Controller class
 */
public class ClientHandler extends ChannelObservable implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    protected ServerStarter serverStarter;//object which created this thread
    protected Socket clientSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private boolean stop = false;

    @Getter
    private final UserService userService;
    @Getter
    private final AuthenticationManager authenticationManager;

    @Getter
    private Map<String, String> clientInfo;

    public ClientHandler(Socket clientSocket, ServerStarter serverStarter,
                         AuthenticationManager authenticationManager, UserService userService) {
        this.clientSocket = clientSocket;
        this.serverStarter = serverStarter;

        this.authenticationManager = authenticationManager;
        this.userService = userService;

        clientInfo = new HashMap<>();

    }

    /**
     * separation for better unit testing
     */
    public void connect() {
        try {
            out = getOutputStream(clientSocket);
            in = getInputStream(clientSocket);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * separation for better unit testing
     */
    protected ObjectOutputStream getOutputStream(Socket clientSocket) throws IOException {
        return new ObjectOutputStream(clientSocket.getOutputStream());
    }

    /**
     * separation for better unit testing
     */
    protected ObjectInputStream getInputStream(Socket clientSocket) throws IOException {
        return new ObjectInputStream(clientSocket.getInputStream());
    }

    public synchronized void sendToClient(Object msg) {
        try {
            if (clientSocket == null || out == null) {
                throw new ServerConnectionException("no socket");
            }

            out.writeObject(msg);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerConnectionException("Connection lost!", e);
        }

    }

    public synchronized Object receiveFromClient() {
        try {
            return in.readObject();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return null;
    }

    public void run() {
        try {
            Object msg;

            while (!stop) {
                msg = in.readObject();
                handleMessageFromClient(msg);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            close();
        }
    }

    protected void handleMessageFromClient(Object obj) {
        String msg = (String) obj;
        String[] line = msg.split("#");

        if (msg.startsWith("close")) {
            String user = clientInfo.get("login");

            if (user != null) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "closing connection with user:" + user);
                }
            } else {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "closing connection");
                }
            }

            close();
        } else if (msg.startsWith("login")) {
            String userName = line[1];
            String password = line[2];

            handleLogin(userName, password);
        } else if (clientInfo.containsKey("login")) {//only logged in user can query data
            String entityName = line[1];
            String command = line[0];

            handleCommand(entityName, command);
        } else {
            sendToClient("Invalid request");
        }
    }

    private void handleLogin(String userName, String password) {
        try {
            //check if user is logged in
            if (serverStarter.isMatchingClient("login", userName)) {
                sendToClient(new LoggedInException(userName + " is already logged in!"));
            } else if (authenticationManager.validateUser(userName, password)) {
                authenticateUser(userName);
            } else {
                sendToClient(new InvalidCredentialsException("Invalid credentials. Try again!"));
            }
        } catch (BllException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Exception sent to client: " + e.getMessage());
            }
            sendToClient(e);
        }
    }

    private void handleCommand(String entityName, String command) {
        try {
            GenericService service = getService(entityName);
            Controller controller = createController(service, entityName);
            executeMethod(controller, command);
        } catch (InvocationTargetException e) {//every exception thrown is wrapped in this one
            Throwable cause = e.getCause();
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Exception sent to client: " + cause.getMessage());
            }
            sendToClient(cause);
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    private void authenticateUser(String userName) {
        clientInfo.put("login", userName);
        clientInfo.put("role", userService.findByUserName(userName).getUserRole().getName());

        sendToClient(clientInfo.get("role"));
        sendToClient(userService.findByUserName(userName));

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "new connected client:" + userName);
        }

        serverStarter.addConnection(clientInfo);

        //notify bll.observer
        notifyObserversWithMessage(Channel.LOGIN, "login#" + userName);
    }

    protected void executeMethod(Controller controller, String command) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        Method methodToExecute = controller.getClass().getMethod(command);

        //check if there is permission to execute this method
        if (checkPermission(controller, methodToExecute)) {
            invokeMethod(controller, methodToExecute);
        } else {
            //don't trow exception if permission is denied, just log it
            sendToClient(new AccessDeniedException("Access Denied!"));
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "access denied for user " + clientInfo.get("login" + " controller: " +
                        controller.getClass().getSimpleName() + " command " + command));
            }
        }
    }

    protected void invokeMethod(Controller controller, Method methodToExecute) throws InvocationTargetException, IllegalAccessException {
        methodToExecute.invoke(controller);
    }

    protected boolean checkPermission(Controller controller, Method methodToExecute) {
        String thisRole = clientInfo.get("role");

        //check permission on method first
        if (methodToExecute.isAnnotationPresent(RequestAccess.class)) {
            RequestAccess ta = methodToExecute.getAnnotation(RequestAccess.class);
            return Arrays.stream(ta.role()).anyMatch(role -> role.equals(thisRole) || "all".equals(role));
        }

        //then if method is not annotated, check it on class level
        if (controller.getClass().isAnnotationPresent(RequestAccess.class)) {
            RequestAccess ta = controller.getClass().getAnnotation(RequestAccess.class);
            return Arrays.stream(ta.role()).anyMatch(role -> role.equals(thisRole) || "all".equals(role));
        }

        return true;//default value is "all" - access for everybody
    }

    /*
    Uses reflection to get a service from the starter class.In this way, code is more decoupled, and there is no
   Open Closed Principle Violation.
     */
    protected GenericService getService(String entityName) throws IllegalAccessException, NoSuchFieldException {
        Field field = serverStarter.getClass().getDeclaredField(Character.toLowerCase(entityName.charAt(0))
                + entityName.substring(1) + "Service");
        field.setAccessible(true);
        return (GenericService) field.get(serverStarter);
    }

    /*
   Uses reflection to create a controller. In this way, code is more decoupled, and there is no
   Open Closed Principle Violation.
    */
    protected Controller createController(GenericService service, String entityName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class type = Class.forName("request.controllers." + entityName + "Controller");
        Constructor<?> cons = type.getConstructor(GenericService.class, ClientHandler.class);
        return (Controller) cons.newInstance(service, this);
    }

    final public void close() {
        stop = true;

        try {
            serverStarter.removeConnection(this);
            if (clientInfo.containsKey("login")) {//notify observers that this was closed
                notifyObserversWithMessage(Channel.CLOSE, "closeNotification#" + clientInfo.get("login"));
            }

            if (clientSocket != null) {
                clientSocket.close();
            }

            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}
