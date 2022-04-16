package model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UserRoleTest {
    @Test
    public void testAccesors() {
        PojoTestUtils.validateAccessors(UserRole.class);
        assertTrue("comes here when no exception was thrown", true);
    }
}