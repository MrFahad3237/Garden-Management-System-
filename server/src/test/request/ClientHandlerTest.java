package request;

import bll.AuthenticationManager;
import bll.exceptions.AccessDeniedException;
import bll.exceptions.InvalidCredentialsException;
import bll.exceptions.LoggedInException;
import bll.exceptions.ServerConnectionException;
import bll.observer.Channel;
import bll.service.PlantService;
import bll.service.UserPlantRequestService;
import bll.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import request.controllers.PlantController;
import request.controllers.UserController;
import request.controllers.UserPlantRequestController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientHandlerTest {
    private ClientHandler clientHandler;

    @Mock
    private Socket clientSocket;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServerStarter serverStarter;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserService userService;
    @Mock
    private ObjectInputStream in;
    @Mock
    private ObjectOutputStream out;

    @Before
    public void setUp() throws Exception {
        //must spy it because some inputstream/outputstream methods are final and cannot mock them
        clientHandler = spy(new ClientHandler(clientSocket, serverStarter, authenticationManager, userService));
        doReturn(in).when(clientHandler).getInputStream(clientSocket);
        doReturn(out).when(clientHandler).getOutputStream(clientSocket);

        clientHandler.connect();

        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test(expected = ServerConnectionException.class)
    public void testSendToClientNull() {
        clientHandler.clientSocket = null;

        clientHandler.sendToClient("msg");
    }

    @Test
    public void testHandleMessageFromClientClose() throws IOException {
        clientHandler.handleMessageFromClient("close");

        verify(in).close();
        verify(out).close();
        verify(clientSocket).close();
    }

    @Test
    public void testHandleMessageFromClientCloseLoggedIn() throws IOException {
        logIn("admin");
        clientHandler.handleMessageFromClient("close");

        verify(clientHandler).notifyObserversWithMessage(eq(Channel.CLOSE), anyString());
    }

    @Test
    public void testHandleMessageFromClientInvalidRequest() throws IOException {
        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked

        clientHandler.handleMessageFromClient("invalid");

        verify(clientHandler).sendToClient("Invalid request");
    }

    @Test
    public void testHandleMessageFromClientLoginDuplicate() {
        final String username = "user";
        final String pass = "pass";

        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked
        when(serverStarter.isMatchingClient("login", username)).thenReturn(true);

        clientHandler.handleMessageFromClient("login#" + username + "#" + pass);

        ArgumentCaptor<LoggedInException> captor = ArgumentCaptor.forClass(LoggedInException.class);
        verify(clientHandler).sendToClient(captor.capture());
        assertEquals("user is logged in","user is already logged in!", captor.getValue().getMessage());
    }

    @Test
    public void testHandleMessageFromClientLoginInvalidCredentials() {
        final String username = "user";
        final String pass = "pass";

        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked
        when(authenticationManager.validateUser(username, pass)).thenReturn(false);
        when(serverStarter.isMatchingClient("login", username)).thenReturn(false);

        clientHandler.handleMessageFromClient("login#" + username + "#" + pass);

        ArgumentCaptor<InvalidCredentialsException> captor = ArgumentCaptor.forClass(InvalidCredentialsException.class);
        verify(clientHandler).sendToClient(captor.capture());
        assertEquals("Invalid credentials.","Invalid credentials. Try again!", captor.getValue().getMessage());
    }

    @Test
    public void testHandleMessageFromClientLoginBllException() {
        final String username = "user";
        final String pass = "pass";

        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked
        when(authenticationManager.validateUser(username, pass)).thenThrow(new ServerConnectionException("server error"));
        when(serverStarter.isMatchingClient("login", username)).thenReturn(false);

        clientHandler.handleMessageFromClient("login#" + username + "#" + pass);

        verify(clientHandler).sendToClient(any(ServerConnectionException.class));
    }

    @Test
    public void testHandleMessageFromClientLogin() {
        final String username = "user";
        final String pass = "pass";

        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked
        when(clientHandler.getAuthenticationManager().validateUser(username, pass)).thenReturn(true);
        when(serverStarter.isMatchingClient("login", username)).thenReturn(false);
        when(clientHandler.getUserService().findByUserName(username).getUserRole().getName()).thenReturn("admin");

        clientHandler.handleMessageFromClient("login#" + username + "#" + pass);

        verify(clientHandler).sendToClient("admin");
        assertEquals("username was saved", username, clientHandler.getClientInfo().get("login"));
    }

    @Test
    public void testHandleCommandPlantFindByIdAdmin() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        final String command = "findById";
        final String entity = "Plant";

        //first log in
        logIn("admin");

        PlantService plantService = mock(PlantService.class);
        PlantController plantController = mock(PlantController.class);

        doReturn(plantService).when(clientHandler).getService(entity);
        doReturn(plantController).when(clientHandler).createController(plantService, entity);

        clientHandler.handleMessageFromClient(command + "#" + entity);

        verify(clientHandler).invokeMethod(eq(plantController), any(Method.class));
    }

    @Test
    public void testHandleCommandUserFindByUsernameAdmin() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        final String command = "findByUsername";
        final String entity = "User";

        //first log in
        logIn("admin");

        UserService plantService = mock(UserService.class);
        UserController plantController = mock(UserController.class);

        doReturn(plantService).when(clientHandler).getService(entity);
        doReturn(plantController).when(clientHandler).createController(plantService, entity);

        clientHandler.handleMessageFromClient(command + "#" + entity);

        verify(clientHandler).invokeMethod(eq(plantController), any(Method.class));
    }

    @Test
    public void testHandleCommandUserFindByUsernameUser() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        final String command = "findByUsername";
        final String entity = "User";

        //first log in
        logIn("user");//user does not have access right to UserController#findByUsername

        UserService service = mock(UserService.class);
        UserController controller = mock(UserController.class);

        doReturn(service).when(clientHandler).getService(entity);
        doReturn(controller).when(clientHandler).createController(service, entity);

        clientHandler.handleMessageFromClient(command + "#" + entity);

        verify(clientHandler, never()).invokeMethod(eq(controller), any(Method.class));
        verify(clientHandler, atLeastOnce()).sendToClient(any(AccessDeniedException.class));
    }

    @Test
    public void testHandleCommandUserPlantRequestFindAllAcceptedAdmin() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        final String command = "findAllAccepted";
        final String entity = "UserPlantRequest";

        //first log in
        logIn("admin");

        UserPlantRequestService service = mock(UserPlantRequestService.class);
        UserPlantRequestController controller = mock(UserPlantRequestController.class);

        doReturn(service).when(clientHandler).getService(entity);
        doReturn(controller).when(clientHandler).createController(service, entity);

        clientHandler.handleMessageFromClient(command + "#" + entity);

        verify(clientHandler).invokeMethod(eq(controller), any(Method.class));
    }

    @Test
    public void testExecuteMethodException() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        final String command = "findAllAccepted";
        final String entity = "UserPlantRequest";

        //first log in
        logIn("admin");

        UserPlantRequestController controller = mock(UserPlantRequestController.class);

        doThrow(new RuntimeException()).when(clientHandler).getService(entity);

        clientHandler.handleMessageFromClient(command + "#" + entity);

        verify(clientHandler, never()).invokeMethod(eq(controller), any(Method.class));
    }

    private void logIn(String role) {
        final String username = "user";
        final String pass = "pass";

        doNothing().when(clientHandler).sendToClient(anyObject());//must mock this because "writeObject" method is final and cannot be mocked
        when(authenticationManager.validateUser(username, pass)).thenReturn(true);
        when(serverStarter.isMatchingClient("login", username)).thenReturn(false);
        when(userService.findByUserName(username).getUserRole().getName()).thenReturn(role);

        clientHandler.handleMessageFromClient("login#" + username + "#" + pass);
    }
}