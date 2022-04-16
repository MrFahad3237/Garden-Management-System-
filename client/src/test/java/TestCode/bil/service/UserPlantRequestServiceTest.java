package TestCode.bil.service;

import Controller.service.UserPlantRequestService;
import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.Plant;
import model.User;
import model.UserPlantRequest;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPlantRequestServiceTest {
    private UserPlantRequestService service;
    private Plant plant = new Plant(1L, "a", 1, 1, 1, 1, new ArrayList<>());
    private User user = new User(1L, "a", "a", new UserRole("user", new HashSet<>()));
    private UserPlantRequest userPlantRequest = new UserPlantRequest(plant, user, 10L);
    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new UserPlantRequestService(client);
        userPlantRequest.setId(1L);

        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test
    public void testAcceptRequest() {
        service.acceptRequest(1L);

        verify(client).sendToServer("acceptRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test(expected = BllException.class)
    public void testAcceptRequestBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        service.acceptRequest(1L);

        verify(client).sendToServer("acceptRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testAcceptRequestException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));

        service.acceptRequest(1L);

        verify(client).sendToServer("acceptRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testDenyRequest() {
        when(client.receiveFromServer()).thenReturn(userPlantRequest);

        service.denyRequest(1L);

        verify(client).sendToServer("denyRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test(expected = BllException.class)
    public void testDenyRequestBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        service.denyRequest(1L);

        verify(client).sendToServer("denyRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testDenyRequestException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));
        service.denyRequest(1L);

        verify(client).sendToServer("denyRequest#UserPlantRequest");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testFindAllAccepted() {
        List<UserPlantRequest> list = Arrays.asList(userPlantRequest);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findAllAccepted());

        verify(client).sendToServer("findAllAccepted#UserPlantRequest");
    }

    @Test(expected = BllException.class)
    public void testFindAllAcceptedBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("socket error"));

        assertEquals("null return", null, service.findAllAccepted());

        verify(client).sendToServer("findAllAccepted#UserPlantRequest");
    }

    @Test
    public void testFindAllAcceptedException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("socket error"));

        assertEquals("null return", null, service.findAllAccepted());

        verify(client).sendToServer("findAllAccepted#UserPlantRequest");
    }

    @Test
    public void testFindAllDenied() {
        List<UserPlantRequest> list = Arrays.asList(userPlantRequest);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findAllDenied());

        verify(client).sendToServer("findAllDenied#UserPlantRequest");
    }

    @Test(expected = BllException.class)
    public void testFindAllDeniedBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("server error"));

        assertEquals("null return", null, service.findAllDenied());

        verify(client).sendToServer("findAllDenied#UserPlantRequest");
    }

    @Test
    public void testFindAllDeniedException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("server error"));

        assertEquals("null return", null, service.findAllDenied());

        verify(client).sendToServer("findAllDenied#UserPlantRequest");
    }

    @Test
    public void testFindNotAccepted() {
        List<UserPlantRequest> list = Arrays.asList(userPlantRequest);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findNotAccepted());

        verify(client).sendToServer("findNotAccepted#UserPlantRequest");
    }

    @Test(expected = BllException.class)
    public void testFindNotAcceptedBllException() {
        when(client.receiveFromServer()).thenThrow(new BllException("server error"));

        assertEquals("correct return", null, service.findNotAccepted());

        verify(client).sendToServer("findNotAccepted#UserPlantRequest");
    }

    @Test
    public void testFindNotAcceptedException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("server error"));

        assertEquals("correct return", null, service.findNotAccepted());

        verify(client).sendToServer("findNotAccepted#UserPlantRequest");
    }

}