package request.controllers;

import bll.service.GenericService;
import model.Plot;
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
public class PlotControllerTest {
    @Mock
    private ClientHandler clientHandler;
    @Mock
    private GenericService<Plot> service;

    private Plot model;

    private PlotController controller;

    @Before
    public void setUp() throws Exception {
        controller = new PlotController(service, clientHandler);
        model = new Plot(1, 2, 3, 4);
    }

    @Test
    public void testFindById() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);
        when(service.findById(id)).thenReturn(model);

        controller.findById();

        verify(clientHandler).sendToClient(model);
    }

    @Test
    public void testFindAll() {
        List<Plot> models = Arrays.asList(model);

        when(service.findAll()).thenReturn(models);

        controller.findAll();

        verify(clientHandler).sendToClient(models);
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

        when(clientHandler.receiveFromClient()).thenReturn(id).thenReturn(model);

        controller.update();

        verify(service).update(id, model);
        verify(clientHandler).sendToClient(model);
    }

    @Test
    public void testSave() {
        Plot savedModel = new Plot(4, 4, 4, 4);

        when(clientHandler.receiveFromClient()).thenReturn(model);
        when(service.save(model)).thenReturn(savedModel);

        controller.save();

        verify(service).save(model);
        verify(clientHandler).sendToClient(savedModel);
    }
}