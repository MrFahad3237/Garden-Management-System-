package model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UserPlantRequestTest {
    @Test
    public void testAccesors() {
        PojoTestUtils.validateAccessors(UserPlantRequest.class);
        assertTrue("comes here when no exception was thrown", true);
    }
}