package bll.service;

import bll.exceptions.DuplicateEntityException;
import gateway.GenericGateway;
import model.User;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    private UserRole role = new UserRole("user", new HashSet<>());
    private UserRole role2 = new UserRole("admin", new HashSet<>());
    private User user = new User("a", "a", role);
    private User user2 = new User("b", "b", role);
    private User user3 = new User("b", "b", role2);

    @Mock
    private GenericGateway<User> gateway;

    private UserService service;

    @Before
    public void setUp() throws Exception {
        service = new UserService(gateway);
        user.setId(1L);
        user2.setId(1L);
        user3.setId(2L);
    }

    @Test
    public void testSave() {
        when(gateway.save(user)).thenReturn(user2);

        assertEquals("correct result", user2, service.save(user));
    }

    @Test(expected = DuplicateEntityException.class)
    public void testSaveDuplicate() {
        List<User> list = Arrays.asList(user);

        when(gateway.save(user)).thenReturn(user2);
        when(gateway.findAll()).thenReturn(list);

        service.save(user);
    }

    @Test
    public void testFindByUserName() {
        List<User> list = Arrays.asList(user, user3);

        when(gateway.findAll()).thenReturn(list);

        assertEquals("user was found", user, service.findByUserName("a"));
    }

    @Test
    public void testFindByUserNameNull() {
        when(gateway.findAll()).thenReturn(new ArrayList<>());

        assertEquals("null was returned", null, service.findByUserName("a"));
    }

    @Test
    public void testFindRegularUsers() {
        List<User> list = Arrays.asList(user, user3);

        when(gateway.findAll()).thenReturn(list);

        assertEquals("there is 1 regular user", 1, service.findRegularUsers().size());
    }
}