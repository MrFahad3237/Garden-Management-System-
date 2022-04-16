package request;

import bll.AuthenticationManager;
import bll.observer.Channel;
import bll.service.PlantService;
import bll.service.PlantedPlantService;
import bll.service.PlotService;
import bll.service.RoleService;
import bll.service.UserPlantRequestService;
import bll.service.UserService;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStarter implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(ServerStarter.class.getName());

    private ServerSocket serverSocket;
    private ServerSocket serverSocketNotification;
    private List<ClientHandler> clientHandlers;
    private List<NotificationHandler> notificationHandlers;
    private boolean stop = false;
    /*
    field names should be lowercase + "Service" because reflection is used
     */
    @Getter
    private final UserService userService;
    @Getter
    private final PlantService plantService;
    @Getter
    private final RoleService userRoleService;
    @Getter
    private final PlotService plotService;
    @Getter
    private final PlantedPlantService plantedPlantService;
    @Getter
    private final UserPlantRequestService userPlantRequestService;
    @Getter
    private final AuthenticationManager authenticationManager;

    public ServerStarter(int port, int portNotification, UserService userService, PlantService plantService, RoleService userRoleService,
                         PlotService plotService, PlantedPlantService plantedPlantService,
                         UserPlantRequestService userPlantRequestService,
                         AuthenticationManager authenticationManager) {

        this.userService = userService;
        this.plantService = plantService;
        this.userRoleService = userRoleService;
        this.plotService = plotService;
        this.plantedPlantService = plantedPlantService;
        this.userPlantRequestService = userPlantRequestService;

        this.authenticationManager = authenticationManager;

        clientHandlers = new ArrayList<>();
        notificationHandlers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            serverSocketNotification = new ServerSocket(portNotification);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        Thread connectionListener = new Thread(this);
        connectionListener.start();
    }

    public void stop() throws IOException {
        stop = true;
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    public void run() {
        while (!stop) {
            try {
                ClientHandler handler = new ClientHandler(serverSocket.accept(), this,
                        authenticationManager, userService);
                handler.connect();
                clientHandlers.add(handler);

                //add every notificationhandler as bll.observer to this clienthandler
                for (NotificationHandler notificationHandler : notificationHandlers) {
                    handler.addObserverToChannel(Channel.CLOSE, notificationHandler);
                    handler.addObserverToChannel(Channel.DATA_CHANGE, notificationHandler);

                    if (notificationHandler.getClientInfo().get("role").equals("admin")) {//admin receives messages when a user logs in
                        handler.addObserverToChannel(Channel.LOGIN, notificationHandler);
                        handler.addObserverToChannel(Channel.PLANT_AT, notificationHandler);
                        handler.addObserverToChannel(Channel.STANDING, notificationHandler);
                    }
                }

                //start the thread only after handlers are assigned
                new Thread(handler).start();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    void removeConnection(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    void removeConnection(NotificationHandler notificationHandler) {
        notificationHandlers.remove(notificationHandler);

        //remove from observables
        for (ClientHandler clientHandler : clientHandlers) {
            for (Channel channel : notificationHandler.getChannelsListeningTo()) {
                clientHandler.removeObserverFromChannel(channel, notificationHandler);
            }
        }
    }

    void addConnection(Map<String, String> clientInfo) {
        try {
            NotificationHandler notificationHandler = new NotificationHandler(serverSocketNotification.accept(), this, clientInfo);

            //add notificationhandler as bll.observer to every clienthandler
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.addObserverToChannel(Channel.CLOSE, notificationHandler);
                clientHandler.addObserverToChannel(Channel.DATA_CHANGE, notificationHandler);
            }

            notificationHandler.addListeningChannel(Channel.CLOSE);
            notificationHandler.addListeningChannel(Channel.DATA_CHANGE);

            if (clientInfo.get("role").equals("admin")) {//admin receives messages when a user logs in
                for (ClientHandler clientHandler : clientHandlers) {
                    notificationHandler.addListeningChannel(Channel.STANDING);//should notify even itself

                    if (!clientHandler.getClientInfo().get("role").equals("admin")) {//shouldn't notify itself
                        clientHandler.addObserverToChannel(Channel.LOGIN, notificationHandler);
                        clientHandler.addObserverToChannel(Channel.PLANT_AT, notificationHandler);

                        notificationHandler.addListeningChannel(Channel.LOGIN);
                        notificationHandler.addListeningChannel(Channel.PLANT_AT);
                    }

                    clientHandler.addObserverToChannel(Channel.STANDING, notificationHandler);
                }
            }

            notificationHandlers.add(notificationHandler);
            new Thread(notificationHandler).start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    boolean isMatchingClient(String key, String value) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getClientInfo().containsKey(key) &&
                    clientHandler.getClientInfo().get(key).equals(value)) {
                return true;
            }
        }
        return false;
    }
}