package request;

import bll.AuthenticationManager;
import bll.service.RoleService;
import bll.service.UserService;
import bll.validators.PasswordValidator;
import gateway.GenericGateway;
import gateway.PlantGateway;
import gateway.PlantedPlantGateway;
import gateway.PlotGateway;
import gateway.RoleGateway;
import gateway.UserGateway;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import model.User;
import model.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;

public final class LoadDataUtil {
    private LoadDataUtil() {
    }

    public static void initGarden() {
        GenericGateway<Plant> plantGateway = new PlantGateway();
        Plant plant = new Plant("a", 1, 1, 1, 5);
        Plant plant2 = new Plant("b", 1, 1, 1, 0);
        Plant plant3 = new Plant("c", 1, 1, 2, 2);
        Plant plant4 = new Plant("d", 1, 1, 3, 1);
        Plant plant5 = new Plant("e", 1, 1, 4, 1);

        plantGateway.save(plant);
        plantGateway.save(plant2);
        plantGateway.save(plant3);
        plantGateway.save(plant4);
        plantGateway.save(plant5);

        GenericGateway<PlantedPlant> plantedPlantGateway = new PlantedPlantGateway();
        plantedPlantGateway.save(new PlantedPlant(0, 0, plant));
        plantedPlantGateway.save(new PlantedPlant(2, 2, plant2));
        plantedPlantGateway.save(new PlantedPlant(3, 3, plant2));
        //
        //
        GenericGateway<Plot> plotGateway = new PlotGateway();
        plotGateway.save(new Plot(6, 4, 0, 0));
        plotGateway.save(new Plot(4, 2, 10, 10));
        plotGateway.save(new Plot(2, 3, 0, 10));
        plotGateway.save(new Plot(4, 2, 10, 0));

        plotGateway.save(new Plot(2, 2, 6, 6));
        plotGateway.save(new Plot(2, 2, 9, 6));
        plotGateway.save(new Plot(4, 4, 4, 9));
    }

    public static void initUsers() {
        RoleService roleService = new RoleService(new RoleGateway());
        UserService userService = new UserService(new UserGateway());
        AuthenticationManager controller = new AuthenticationManager(userService,
                new BCryptPasswordEncoder(), new PasswordValidator());

        UserRole role1 = new UserRole("admin", new HashSet<>());
        UserRole role2 = new UserRole("user", new HashSet<>());

        User user1 = new User("admin", controller.encode("admin"), role1);
        User user2 = new User("user1", controller.encode("user1"), role2);
        User user3 = new User("user2", controller.encode("user2"), role2);
        User user4 = new User("ttt", controller.encode("ttt"), role2);

        roleService.save(role1);
        roleService.save(role2);

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);

    }

}
