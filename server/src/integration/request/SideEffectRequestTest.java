package request;

import gateway.HibernateUtil;
import model.Plant;
import model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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
public class SideEffectRequestTest {
    private static boolean setUpIsDone = false;

    @Before
    public void initBefore() throws Exception {
        loadData();

        if (setUpIsDone) {
            return;
        }
        // do the setup
        logIn();
        setUpIsDone = true;
    }

    @BeforeClass
    public static void init() throws Exception {
        startServer();
        startClient();
    }

    @Test
    public void testSavePlant() {
        Plant plant = new Plant("ooo", 1, 1, 1, 1);
        clientConnection.sendToServer("save#Plant");
        clientConnection.sendToServer(plant);

        Plant plant2 = (Plant) clientConnection.receiveFromServer();

        assertEquals("same fields", plant.toString(), plant2.toString());
    }

    @Test
    public void testUpdatePlant() {
        Plant plant = new Plant("uuu", 1, 1, 1, 1);
        clientConnection.sendToServer("update#Plant");
        clientConnection.sendToServer(1L);
        clientConnection.sendToServer(plant);

        Plant plant2 = (Plant) clientConnection.receiveFromServer();

        assertEquals("same fields", plant.toString(), plant2.toString());
    }

    @Test
    public void testDeletePlant() {
        clientConnection.sendToServer("delete#Plant");
        clientConnection.sendToServer(3L);

        clientConnection.receiveFromServer();

        //check if it was deleted
        clientConnection.sendToServer("findAll#Plant");

        List<Plant> plants = (List<Plant>) clientConnection.receiveFromServer();

        assertEquals("4 plants remained", 4, plants.size());
    }

    @After
    public void closeAfter() throws IOException {
        HibernateUtil.close();
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
