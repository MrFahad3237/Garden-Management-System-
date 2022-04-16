package request;

import bll.AuthenticationManager;
import bll.service.PlantService;
import bll.service.PlantedPlantService;
import bll.service.PlotService;
import bll.service.RoleService;
import bll.service.UserPlantRequestService;
import bll.service.UserService;
import bll.validators.PasswordValidator;
import gateway.PlantGateway;
import gateway.PlantedPlantGateway;
import gateway.PlotGateway;
import gateway.RoleGateway;
import gateway.UserGateway;
import gateway.UserPlantRequestGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class InitUtil {
    private InitUtil() {
    }

    static ClientConnection clientConnection;
    static NotificationConnection notificationConnection;
    static ServerStarter serverStarter;

    static void startClient() throws IOException {
        Properties prop = new Properties();
        String fileName = "src/main/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);

        String ip = prop.getProperty("app.ip");

        clientConnection = new ClientConnection(6666, ip);
        notificationConnection = new NotificationConnection(6667, ip);
    }

    static void startServer() {
        RoleService roleService = new RoleService(new RoleGateway());
        UserService userService = new UserService(new UserGateway());
        PlantService plantService = new PlantService(new PlantGateway());
        PlantedPlantService plantedPlantService = new PlantedPlantService(new PlantedPlantGateway(),
                plantService);
        PlotService plotService = new PlotService(new PlotGateway());
        UserPlantRequestService userPlantRequestService = new UserPlantRequestService(new UserPlantRequestGateway(),
                plantService);

        AuthenticationManager authenticationManager = new AuthenticationManager(userService,
                new BCryptPasswordEncoder(), new PasswordValidator());

        serverStarter = new ServerStarter(6666, 6667, userService,
                plantService, roleService, plotService,
                plantedPlantService, userPlantRequestService, authenticationManager);
    }

    static void loadData() {
        LoadDataUtil.initGarden();
        LoadDataUtil.initUsers();
    }
}
