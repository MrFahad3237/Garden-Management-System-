package request.controllers;

import bll.observer.Channel;
import bll.service.GenericService;
import model.Plant;
import model.PlantedPlant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import request.ClientHandler;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantedPlantControllerTest {

    @Mock
    private ClientHandler clientHandler;
    @Mock
    private GenericService<PlantedPlant> service;

    private Plant dummyPlant;
    private PlantedPlant dummyPlantedPlant;

    private PlantedPlantController controller;

    @Before
    public void setUp() throws Exception {
        controller = new PlantedPlantController(service, clientHandler);
        dummyPlant = new Plant("a", 1, 1, 1, 1);
        dummyPlantedPlant = new PlantedPlant(1L, 1, 1, dummyPlant);
    }

    @Test
    public void testFindById() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);
        when(service.findById(id)).thenReturn(dummyPlantedPlant);

        controller.findById();

        verify(clientHandler).sendToClient(dummyPlantedPlant);
    }

    @Test
    public void testFindAll() {
        List<PlantedPlant> plants = Arrays.asList(dummyPlantedPlant);

        when(service.findAll()).thenReturn(plants);

        controller.findAll();

        verify(clientHandler).sendToClient(plants);
    }

    @Test
    public void testDelete() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);

        controller.delete();

        verify(service).delete(id);
        verify(clientHandler).sendToClient(true);
    }

    @Test
    public void testUpdate() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id).thenReturn(dummyPlantedPlant);

        controller.update();

        verify(service).update(id, dummyPlantedPlant);
        verify(clientHandler).sendToClient(dummyPlantedPlant);
        verify(clientHandler).notifyObserversWithMessage(Channel.DATA_CHANGE, "change#plantedplant");
    }

    @Test
    public void testSave() {
        PlantedPlant savedPlantedPlant = new PlantedPlant(1L, 2,2, dummyPlant);

        when(clientHandler.receiveFromClient()).thenReturn(dummyPlantedPlant);
        when(service.save(dummyPlantedPlant)).thenReturn(savedPlantedPlant);

        controller.save();

        verify(service).save(savedPlantedPlant);
        verify(clientHandler).sendToClient(savedPlantedPlant);
        verify(clientHandler).notifyObserversWithMessage(Channel.DATA_CHANGE, "change#plantedplant");
        verify(clientHandler).notifyObserversWithMessage(eq(Channel.PLANT_AT), any());
    }
}