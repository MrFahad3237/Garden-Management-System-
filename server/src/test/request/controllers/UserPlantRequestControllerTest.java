package request.controllers;

import bll.observer.Channel;
import bll.service.UserPlantRequestService;
import model.Plant;
import model.User;
import model.UserPlantRequest;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import request.ClientHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPlantRequestControllerTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ClientHandler clientHandler;
    @Mock
    private UserPlantRequestService service;

    private UserPlantRequest model;
    private Plant dummyPlant;
    private User dummyUser;

    private UserPlantRequestController controller;

    @Before
    public void setUp() throws Exception {
        controller = new UserPlantRequestController(service, clientHandler);
        dummyPlant = new Plant("a", 1, 1, 1, 1);
        UserRole regularUser = new UserRole("user", new HashSet<>());
        dummyUser = new User("a", "a", regularUser);
        model = new UserPlantRequest(dummyPlant, dummyUser, 10L);
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
        List<UserPlantRequest> models = Arrays.asList(model);

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
        UserPlantRequest savedModel = new UserPlantRequest(dummyPlant, dummyUser, 100L);

        when(clientHandler.receiveFromClient()).thenReturn(model);
        when(service.save(model)).thenReturn(savedModel);

        controller.save();

        verify(service).save(model);
        verify(clientHandler).sendToClient(savedModel);
        verify(clientHandler).notifyObserversWithMessage(Channel.STANDING, "save");
    }

    @Test
    public void testFindAllAccepted() {
        List<UserPlantRequest> models = Arrays.asList(model);

        when(service.findAllAccepted()).thenReturn(models);

        controller.findAllAccepted();

        verify(clientHandler).sendToClient(models);
    }

    @Test
    public void testFindAllDenied() {
        List<UserPlantRequest> models = Arrays.asList(model);

        when(service.findAllDenied()).thenReturn(models);

        controller.findAllDenied();

        verify(clientHandler).sendToClient(models);
    }

    @Test
    public void testFindNotAccepted() {
        List<UserPlantRequest> models = Arrays.asList(model);

        when(service.findNotAccepted()).thenReturn(models);

        controller.findNotAccepted();

        verify(clientHandler).sendToClient(models);
    }

    @Test
    public void testAcceptRequest() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);

        controller.acceptRequest();

        verify(service).acceptRequest(id);
        verify(clientHandler).notifyObserversWithMessage(Channel.STANDING, "acceptRequest");
        verify(clientHandler).notifyObserversWithMessage(Channel.DATA_CHANGE, "acceptRequest");
    }

    @Test
    public void testDenyRequest() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id);

        controller.denyRequest();

        verify(service).denyRequest(id);
        verify(clientHandler).notifyObserversWithMessage(Channel.STANDING, "denyRequest");
    }
}