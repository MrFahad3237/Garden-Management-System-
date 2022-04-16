package TestCode.bil.service;

import Controller.service.UserService;
import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.User;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    private UserService service;
    private User user = new User(1L, "a", "a", new UserRole("user", new HashSet<>()));
    private User user2 = new User(1L, "b", "b", new UserRole("user", new HashSet<>()));

    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new UserService(client);

        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test
    public void testFindByUserName() {
        when(client.receiveFromServer()).thenReturn(user);

        assertEquals("correct return", user, service.findByUserName("a"));

        verify(client).sendToServer("findByUsername#User");
        verify(client).sendToServer("a");
    }

    @Test(expected = BllException.class)
    public void testFindByUserNameBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        assertEquals("null return", null, service.findByUserName("a"));

        verify(client).sendToServer("findByUsername#User");
        verify(client).sendToServer("a");
    }

    @Test
    public void testFindByUserNameException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));

        assertEquals("null return", null, service.findByUserName("a"));

        verify(client).sendToServer("findByUsername#User");
        verify(client).sendToServer("a");
    }

    @Test
    public void testFindRegularUsers() {
        List<User> list = Arrays.asList(user);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findRegularUsers());

        verify(client).sendToServer("findRegularUsers#User");
    }

    @Test(expected = BllException.class)
    public void testFindRegularUsersBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        assertEquals("null return", null, service.findRegularUsers());

        verify(client).sendToServer("findRegularUsers#User");
    }

    @Test
    public void testFindRegularUsersException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));

        assertEquals("null return", null, service.findRegularUsers());

        verify(client).sendToServer("findRegularUsers#User");
    }

    @Test
    public void testUpdate() {
        when(client.receiveFromServer()).thenReturn(user2);

        assertEquals("correct return", user2, service.update(1L, user, true));

        verify(client).sendToServer("update#User");
        verify(client).sendToServer(user);
        verify(client).sendToServer(true);
        verify(client).sendToServer(1L);
    }

    @Test(expected = BllException.class)
    public void testUpdateBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        assertEquals("null return", null, service.update(1L, user, true));

        verify(client).sendToServer("update#User");
        verify(client).sendToServer(user);
        verify(client).sendToServer(true);
        verify(client).sendToServer(1L);
    }

    @Test
    public void testUpdateException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));

        assertEquals("null return", null, service.update(1L, user, true));

        verify(client).sendToServer("update#User");
        verify(client).sendToServer(user);
        verify(client).sendToServer(true);
        verify(client).sendToServer(1L);
    }
}