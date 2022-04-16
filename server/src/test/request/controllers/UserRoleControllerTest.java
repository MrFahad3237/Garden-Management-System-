package request.controllers;

import bll.service.RoleService;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import request.ClientHandler;

import java.util.HashSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRoleControllerTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ClientHandler clientHandler;
    @Mock
    private RoleService service;

    private UserRole regularUser;

    private UserRoleController controller;

    @Before
    public void setUp() throws Exception {
        controller = new UserRoleController(service, clientHandler);
        regularUser = new UserRole("user", new HashSet<>());
    }

    @Test
    public void testFindByName() {
        when(clientHandler.receiveFromClient()).thenReturn("user");
        when(service.findByName("user")).thenReturn(regularUser);

        controller.findByName();

        verify(clientHandler).sendToClient(regularUser);
    }
}