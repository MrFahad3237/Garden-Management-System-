package request;

import bll.exceptions.InvalidCredentialsException;
import model.Plant;
import model.User;
import model.UserRole;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static request.InitUtil.loadData;
import static request.InitUtil.startServer;
import static request.InitUtil.startClient;
import static request.InitUtil.clientConnection;
import static request.InitUtil.notificationConnection;
import static request.InitUtil.serverStarter;

/**
 * Integration test classes should be run separately, to reset the Hibernate configuration
 * Also, create-drop option in the configuration file must be enabled to recreate the tables every time
 */
public class NoSideEffectRequestTest {

    @BeforeClass
    public static void init() throws Exception {
        loadData();
        startServer();
        startClient();
        logIn();
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testIncorrectLogin() {
        final String userName = "invalid";
        final String password = "invalid";

        clientConnection.sendToServer("login#" + userName + "#" + password);

        clientConnection.receiveFromServer();
    }

    @Test
    public void testFindByNameRole() {
        clientConnection.sendToServer("findByName#UserRole");
        clientConnection.sendToServer("user");

        UserRole role = (UserRole) clientConnection.receiveFromServer();

        assertEquals("correct role", "user", role.getName());
    }

    @Test
    public void testFindByIdPlant() {
        clientConnection.sendToServer("findById#Plant");
        clientConnection.sendToServer(1L);

        Plant plant = (Plant) clientConnection.receiveFromServer();

        assertEquals("correct type", "a", plant.getType());
    }

    @Test
    public void testFindAllPlant() {
        clientConnection.sendToServer("findAll#Plant");

        List<Plant> plants = (List<Plant>) clientConnection.receiveFromServer();

        assertEquals("5 plants were inserted", 5, plants.size());
    }

    @AfterClass
    public static void close() throws IOException {
        clientConnection.closeConnection();
        serverStarter.stop();
    }

    private static void logIn() {
        final String userName = "user1";
        final String password = "user1";

        clientConnection.sendToServer("login#" + userName + "#" + password);

        clientConnection.receiveFromServer();
        User user = (User) clientConnection.receiveFromServer();

        //start notificationhandler
        new Thread(notificationConnection).start();

        assertEquals("correct login", userName, user.getUsername());
    }
}
