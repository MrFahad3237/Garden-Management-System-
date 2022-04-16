package Controller;

import Controller.bill.AuthenticationManager;
import Controller.execptions.GlobalExceptionHandlerUtil;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.RoleService;
import Controller.service.UserPlantRequestService;
import Controller.service.UserService;
import model.connection.ClientConnection;
import model.connection.ClientConnectionImpl;
import model.connection.NotificationConnection;
import View.LoginController;
import View.LoginViewImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {

        GlobalExceptionHandlerUtil.enableGlobalException();

        Properties prop = new Properties();
        String fileName = "src/main/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);

        String ip = prop.getProperty("app.ip");

        final ClientConnection clientConnection = new ClientConnectionImpl(6666, ip);
        final NotificationConnection notificationConnection = new NotificationConnection(6667, ip);

        final RoleService roleService = new RoleService(clientConnection);
        final UserService userService = new UserService(clientConnection);
        final PlantService plantService = new PlantService(clientConnection);
        final PlantedPlantService plantedPlantService = new PlantedPlantService(clientConnection);
        final PlotService plotService = new PlotService(clientConnection);
        final UserPlantRequestService requestService = new UserPlantRequestService(clientConnection);

        final AuthenticationManager authenticationManager = new AuthenticationManager(clientConnection,
                notificationConnection);

        LoginController loginController = new LoginController(new LoginViewImpl(), userService,
                plantService, roleService, plantedPlantService,
                plotService, requestService, authenticationManager);

        loginController.logIn();

    }
}

