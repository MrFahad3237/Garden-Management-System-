package request.controllers;

import bll.service.GenericService;
import model.Plant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import request.ClientHandler;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantControllerTest {
    @Mock
    private ClientHandler clientHandler;
    @Mock
    private GenericService<Plant> service;

    private Plant dummyPlant;

    private PlantController controller;

    @Before
    public void setUp() throws Exception {
        controller = new PlantController(service, clientHandler);
        dummyPlant = new Plant("a", 1, 1, 1, 1);
    }

    @Test
    public void testFindById() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);
        when(service.findById(id)).thenReturn(dummyPlant);

        controller.findById();

        verify(clientHandler).sendToClient(dummyPlant);
    }

    @Test
    public void testFindAll() {
        List<Plant> plants = Arrays.asList(dummyPlant);

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

        when(clientHandler.receiveFromClient()).thenReturn(id).thenReturn(dummyPlant);

        controller.update();

        verify(service).update(id, dummyPlant);
        verify(clientHandler).sendToClient(dummyPlant);
    }

    @Test
    public void testSave() {
        Plant savedPlant = new Plant("o", 2, 2, 2, 2);

        when(clientHandler.receiveFromClient()).thenReturn(dummyPlant);
        when(service.save(dummyPlant)).thenReturn(savedPlant);

        controller.save();

        verify(service).save(dummyPlant);
        verify(clientHandler).sendToClient(savedPlant);
    }
}