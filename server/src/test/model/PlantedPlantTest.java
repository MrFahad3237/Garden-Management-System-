package model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PlantedPlantTest {

    private Plant plant = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "a", 1, 1, 1, 1);
    private Plant plant2 = new Plant(2L, new ArrayList<>(), new ArrayList<>(), "b", 2, 2, 2, 2);

    @Test
    public void testAccesors() {
        PojoTestUtils.validateAccessors(PlantedPlant.class);
        assertTrue("comes here when no exception was thrown", true);
    }

    @Test
    public void testToString() {
        String toString = new PlantedPlant(1L, 1, 1, plant).toString();
        assertTrue("toString() must contain this information",toString.contains("x") && toString.contains("x=") && toString.contains("y="));
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(new PlantedPlant(1L, 1, 1, plant),
                new PlantedPlant(2L, 2, 2, plant2));
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals(new PlantedPlant(1L, 1, 1, plant),
                null);
    }

    @Test
    public void testEqualsSameObject() {
        PlantedPlant plantedPlant = new PlantedPlant(1L, 1, 1, plant);
        assertEquals("same object",plantedPlant,
                plantedPlant);
    }

    @Test
    public void testEquals() {
        assertEquals("same id",new PlantedPlant(1L, 1, 1, plant),
                new PlantedPlant(1L, 2, 2, plant2));
    }

    @Test
    public void testHashCode() {
        PlantedPlant plantedPlant1 = new PlantedPlant(1, 1, plant);
        PlantedPlant plantedPlant2 = new PlantedPlant( 1, 1, plant);
        plantedPlant1.setId(1L);
        plantedPlant2.setId(1L);
        assertSame("same id",plantedPlant1.hashCode(), plantedPlant2.hashCode());
    }

}