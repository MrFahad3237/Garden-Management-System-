package model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PlantTest {
    private Plant plant1 = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "a", 1, 1, 1, 1);
    private Plant plant2 = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "b", 1, 1, 1, 1);
    private Plant plant3 = new Plant(2L, new ArrayList<>(), new ArrayList<>(), "b", 1, 1, 1, 1);

    @Test
    public void testAccesors() {
        PojoTestUtils.validateAccessors(Plant.class);
        assertTrue("comes here when no exception was thrown", true);
    }

    @Test
    public void testNotEquals() {
        assertNotEquals("different id", plant1, plant3);
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals("not equal with null", plant1, null);
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals("same objects are equal", plant1, plant1);
    }

    @Test
    public void testEquals() {
        assertEquals("equality is checked by id", plant1, plant2);
    }

    @Test
    public void testHashCode() {
        assertSame("same id", plant1.hashCode(), plant2.hashCode());
    }
}