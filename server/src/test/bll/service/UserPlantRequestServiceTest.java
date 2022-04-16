package bll.service;

import gateway.GenericGateway;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPlantRequestServiceTest {
    private Plant plant = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "a", 1, 1, 1, 1);
    private UserRole role = new UserRole("user", new HashSet<>());
    private User user = new User("a", "a", role);
    private UserPlantRequest requestAccepted = new UserPlantRequest(plant, user, 10L);
    private UserPlantRequest requestDenied = new UserPlantRequest(plant, user, 101L);
    private UserPlantRequest requestNotAccepted = new UserPlantRequest(plant, user, 101L);

    @Mock
    private GenericGateway<UserPlantRequest> gateway;
    /**
     * this is a mock because it is just a dependency here
     * it will be tested in its own test class
     */
    @Mock
    private PlantService plantService;

    private UserPlantRequestService service;

    @Before
    public void setUp() throws Exception {
        service = new UserPlantRequestService(gateway, plantService);

        requestAccepted.setId(1L);
        requestDenied.setId(2L);
        requestNotAccepted.setId(3L);

        requestAccepted.setStatus("accepted");
        requestDenied.setStatus("denied");
        requestNotAccepted.setStatus(null);
    }

    @Test
    public void testSave() {
        when(gateway.save(requestAccepted)).thenReturn(requestDenied);

        assertEquals("correct result", requestDenied, service.save(requestAccepted));
    }

    @Test
    public void testFindNotAccepted() {
        initFindAll();

        assertTrue("result contains corresponding request", service.findNotAccepted().contains(requestNotAccepted));
    }

    @Test
    public void testFindAllAccepted() {
        initFindAll();

        assertTrue("result contains corresponding request", service.findAllAccepted().contains(requestAccepted));
    }

    @Test
    public void testFindAllDenied() {
        initFindAll();

        assertTrue("result contains corresponding request", service.findAllDenied().contains(requestDenied));
    }

    @Test
    public void testAcceptRequest() {
        when(gateway.findById(3L)).thenReturn(requestNotAccepted);

        service.acceptRequest(3L);

        assertEquals("request status has changed", "accepted", requestNotAccepted.getStatus());
        verify(plantService).update(plant.getId(), plant);//plant stock update
    }

    @Test
    public void testDenyRequest() {
        when(gateway.findById(3L)).thenReturn(requestNotAccepted);

        service.denyRequest(3L);

        assertEquals("request status has changed", "denied", requestNotAccepted.getStatus());
    }

    private void initFindAll() {
        List<UserPlantRequest> list = Arrays.asList(requestAccepted, requestDenied, requestNotAccepted);
        when(gateway.findAll()).thenReturn(list);
    }

}