package TestCode.bil.service;

import Controller.service.PlantService;
import Controller.execptions.ServerConnectionException;
import model.connection.ClientConnection;
import model.Plant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantServiceTest {
    private PlantService service;
    private Plant plant = new Plant(1L, "a", 1, 1, 1, 1, new ArrayList<>());
    private Plant plant2 = new Plant(1L, "b", 1, 1, 1, 1, new ArrayList<>());

    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new PlantService(client);
    }

    @Test
    public void testFindById() {
        when(client.receiveFromServer()).thenReturn(plant);

        assertEquals("correct return", plant, service.findById(1L));

        verify(client).sendToServer("findById#Plant");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testFindAll() {
        List<Plant> list = Arrays.asList(plant);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findAll());

        verify(client).sendToServer("findAll#Plant");
    }

    @Test
    public void testSave() {

        when(client.receiveFromServer()).thenReturn(plant2);

        assertEquals("correct return", plant2, service.save(plant));

        verify(client).sendToServer("save#Plant");
        verify(client).sendToServer(plant);
    }

    @Test(expected = ServerConnectionException.class)
    public void testUpdateException() {
        when(client.receiveFromServer()).thenThrow(new ServerConnectionException("server down"));

        service.update(1L, plant);

        verify(client).sendToServer("update#Plant");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plant);
    }

    @Test
    public void testUpdate() {
        service.update(1L, plant);

        verify(client).sendToServer("update#Plant");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plant);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(client).sendToServer("delete#Plant");
        verify(client).sendToServer(1L);
    }
}