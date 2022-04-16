package TestCode.bil.service;

import Controller.service.RoleService;
import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {
    private RoleService service;
    private UserRole userRole = new UserRole("user", new HashSet<>());

    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new RoleService(client);
        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test
    public void testFindByName() {
        when(client.receiveFromServer()).thenReturn(userRole);

        assertEquals("correct return", userRole, service.findByName("user"));

        verify(client).sendToServer("findByName#UserRole");
        verify(client).sendToServer("user");
    }

    @Test(expected = BllException.class)
    public void testFindByNameBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("server timeout"));

        assertEquals("null return", null, service.findByName("user"));

        verify(client).sendToServer("findByName#UserRole");
        verify(client).sendToServer("user");
    }

    @Test
    public void testFindByNameException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("server timeout"));

        assertEquals("null return", null, service.findByName("user"));

        verify(client).sendToServer("findByName#UserRole");
        verify(client).sendToServer("user");
    }
}