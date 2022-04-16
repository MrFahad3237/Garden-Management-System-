package bll.service;

import gateway.GenericGateway;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {
    private UserRole role = new UserRole("user", new HashSet<>());
    private UserRole role2 = new UserRole("user2", new HashSet<>());

    @Mock
    private GenericGateway<UserRole> gateway;

    private RoleService service;

    @Before
    public void setUp() throws Exception {
        service = new RoleService(gateway);
    }

    @Test
    public void testSave() {
        when(gateway.save(role)).thenReturn(role2);

        assertEquals("correct result", role2, service.save(role));
    }

    @Test
    public void testFindByName() {
        List<UserRole> list = Arrays.asList(role, role2);
        when(gateway.findAll()).thenReturn(list);

        assertEquals("correct result", role, service.findByName("user"));
    }

}