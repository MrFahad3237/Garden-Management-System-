package model;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class UserTest {
    @Test
    public void testAccesors() {
        PojoTestUtils.validateAccessors(User.class);
        assertTrue("comes here when no exception was thrown", true);
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals(new User(), null);
    }
}