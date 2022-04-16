package TestCode.bil.service;

import Controller.service.PlantedPlantService;
import Controller.execptions.AccessDeniedException;
import Controller.execptions.InvalidCredentialsException;
import Controller.execptions.OutOfStockException;
import Controller.execptions.ServerConnectionException;
import model.connection.ClientConnection;
import model.Plant;
import model.PlantedPlant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantedPlantServiceTest {
    private PlantedPlantService service;
    private Plant plant = new Plant(1L, "a", 1, 1, 1, 1, new ArrayList<>());
    private PlantedPlant plantedPlant = new PlantedPlant(1L, 1, 1, plant);
    private PlantedPlant plantedPlant2 = new PlantedPlant(1L, 2, 2, plant);

    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new PlantedPlantService(client);
        //disable logging
        LogManager.getLogManager().reset();
    }

    @Test
    public void testFindById() {
        when(client.receiveFromServer()).thenReturn(plantedPlant);

        assertEquals("correct return", plantedPlant, service.findById(1L));

        verify(client).sendToServer("findById#PlantedPlant");
        verify(client).sendToServer(1L);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testFindByIdBllException() {
        when(client.receiveFromServer()).thenThrow(new InvalidCredentialsException("invalid credentials"));

        assertEquals("correct return", plantedPlant, service.findById(1L));

        verify(client).sendToServer("findById#PlantedPlant");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testFindByIdException() {
        when(client.receiveFromServer()).thenThrow(new RuntimeException("invalid credentials"));

        assertEquals("null return", null, service.findById(1L));

        verify(client).sendToServer("findById#PlantedPlant");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testFindAll() {
        List<PlantedPlant> list = Arrays.asList(plantedPlant);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findAll());

        verify(client).sendToServer("findAll#PlantedPlant");
    }

    @Test(expected = AccessDeniedException.class)
    public void testFindAllBllException() {
        when(client.receiveFromServer()).thenThrow(new AccessDeniedException("acces denied"));

        service.findAll();

        verify(client).sendToServer("findAll#PlantedPlant");
    }

    @Test
    public void testFindAllException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("acces denied"));

        service.findAll();

        verify(client).sendToServer("findAll#PlantedPlant");
    }

    @Test
    public void testSave() {

        when(client.receiveFromServer()).thenReturn(plantedPlant2);

        assertEquals("correct return", plantedPlant2, service.save(plantedPlant));

        verify(client).sendToServer("save#PlantedPlant");
        verify(client).sendToServer(plantedPlant);
    }

    @Test(expected = OutOfStockException.class)
    public void testSaveBllException() {

        when(client.receiveFromServer()).thenThrow(new OutOfStockException("plant out of stock"));

        service.save(plantedPlant);

        verify(client).sendToServer("save#PlantedPlant");
        verify(client).sendToServer(plantedPlant);
    }

    @Test
    public void testSaveException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("plant out of stock"));

        service.save(plantedPlant);

        verify(client).sendToServer("save#PlantedPlant");
        verify(client).sendToServer(plantedPlant);
    }

    @Test
    public void testUpdate() {
        service.update(1L, plantedPlant);

        verify(client).sendToServer("update#PlantedPlant");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plantedPlant);
    }

    @Test(expected = ServerConnectionException.class)
    public void testUpdateBllException() {
        when(client.receiveFromServer()).thenThrow(new ServerConnectionException("server down"));

        service.update(1L, plantedPlant);

        verify(client).sendToServer("update#PlantedPlant");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plantedPlant);
    }

    @Test
    public void testUpdateException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("server down"));

        service.update(1L, plantedPlant);

        verify(client).sendToServer("update#PlantedPlant");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plantedPlant);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(client).sendToServer("delete#PlantedPlant");
        verify(client).sendToServer(1L);
    }

    @Test(expected = ServerConnectionException.class)
    public void testDeleteBllException() {
        when(client.receiveFromServer()).thenThrow(new ServerConnectionException("connection timeout", new SocketException()));

        service.delete(1L);

        verify(client).sendToServer("delete#PlantedPlant");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testDeleteException() {
        //test to see if exception is caught
        when(client.receiveFromServer()).thenThrow(new RuntimeException("connection timeout", new SocketException()));

        service.delete(1L);

        verify(client).sendToServer("delete#PlantedPlant");
        verify(client).sendToServer(1L);
    }
}