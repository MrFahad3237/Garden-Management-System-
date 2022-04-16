package request.controllers;

import bll.service.UserService;
import model.User;
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

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ClientHandler clientHandler;
    @Mock
    private UserService service;

    private User model;
    private UserRole regularUser;

    private UserController controller;

    @Before
    public void setUp() throws Exception {
        controller = new UserController(service, clientHandler);
        regularUser = new UserRole("user", new HashSet<>());
        model = new User("a", "a", regularUser);
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
        List<User> models = Arrays.asList(model);

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
    public void testUpdateNotChangePassword() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id).thenReturn(model).thenReturn(false);

        controller.update();

        verify(service).update(id, model);
        verify(clientHandler).sendToClient(model);
    }

    @Test
    public void testUpdateChangePassword() {
        final Long id = 1L;

        when(clientHandler.receiveFromClient()).thenReturn(id).thenReturn(model).thenReturn(true);
        when(clientHandler.getAuthenticationManager().encode(model.getPassword())).thenReturn("encoded");

        controller.update();

        assertEquals("user contains new password", "encoded", model.getPassword());
        verify(service).update(id, model);
        verify(clientHandler).sendToClient(model);
    }

    @Test
    public void testSave() {
        User savedModel = new User("b", "b", regularUser);

        when(clientHandler.receiveFromClient()).thenReturn(model);
        when(clientHandler.getAuthenticationManager().encode(model.getPassword())).thenReturn("encoded");
        when(service.save(model)).thenReturn(savedModel);

        controller.save();

        assertEquals("user contains new password", "encoded", model.getPassword());
        verify(service).save(model);
        verify(clientHandler).sendToClient(savedModel);
    }

    @Test
    public void testFindByUsername() {
        final String userName = "a";

        when(clientHandler.receiveFromClient()).thenReturn(userName);
        when(service.findByUserName(userName)).thenReturn(model);

        controller.findByUsername();

        verify(clientHandler).sendToClient(model);
    }

    @Test
    public void testFindRegularUsers() {
        List<User> models = Arrays.asList(model);

        when(service.findRegularUsers()).thenReturn(models);

        controller.findRegularUsers();

        verify(clientHandler).sendToClient(models);
    }
}