package TestCode.bil.util;

import Controller.bill.AuthenticationManager;
import Controller.execptions.LoggedInException;
import Controller.observer.Channel;
import Controller.observer.ChannelObserver;
import model.connection.Client;
import model.connection.ClientConnection;
import model.connection.NotificationConnection;
import model.User;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.logging.LogManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationManagerTest {

    @Mock
    private ClientConnection clientConnection;
    @Mock
    private NotificationConnection notificationConnection;

    private UserRole adminRole = new UserRole("admin", new HashSet<>());

    private AuthenticationManager authenticationManager;

    @Before
    public void setUp() {
        authenticationManager = new AuthenticationManager(clientConnection,
                notificationConnection);

        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test
    public void validateUser() throws Exception {
        final String username = "username";
        final String password = "password";

        when(clientConnection.receiveFromServer()).thenReturn("admin").
                thenReturn(new User(1L, "admin", "admin", adminRole));

        authenticationManager.validateUser(username, password);

        Client client = authenticationManager.getClient();

        assertTrue("correct user", client.getPassword().equals(password) &&
                client.getUserName().equals(username) && client.getRole().equals("admin"));
        verify(clientConnection).sendToServer("login#" + username + "#" + password);
    }

    @Test(expected = LoggedInException.class)
    public void validateUserLoggedInException() throws Exception {

        final String username = "username";
        final String password = "password";

        when(clientConnection.receiveFromServer()).thenThrow(new LoggedInException("already logged in!"));

        authenticationManager.validateUser(username, password);

        assertTrue("correct user", authenticationManager.getClient().getPassword().equals(password) &&
                authenticationManager.getClient().getUserName().equals(username));
        verify(clientConnection).sendToServer("login#" + username + "#" + password);
    }

    @Test
    public void validateUserException() throws Exception {
        final String username = "username";
        final String password = "password";

        when(clientConnection.receiveFromServer()).thenThrow(new RuntimeException("stream error"));

        authenticationManager.validateUser(username, password);

        assertFalse("validation with success", authenticationManager.validateUser(username, password));
    }

    @Test
    public void testLogout() {

        authenticationManager.logout();

        verify(notificationConnection).close();
        verify(clientConnection).closeConnection();
    }

    @Test
    public void testAddNotificationObserver() {
        ChannelObserver observer = mock(ChannelObserver.class);

        authenticationManager.addNotificationObserver(Channel.LOGIN, observer);

        verify(notificationConnection).addObserverToChannel(Channel.LOGIN, observer);
    }

    @Test
    public void testRemoveNotificationObserver() {
        ChannelObserver observer = mock(ChannelObserver.class);

        authenticationManager.removeNotificationObserver(Channel.DATA_CHANGE, observer);

        verify(notificationConnection).removeObserverFromChannel(Channel.DATA_CHANGE, observer);
    }
}