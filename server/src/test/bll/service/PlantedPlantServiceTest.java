package bll.service;

import bll.exceptions.OutOfStockException;
import bll.exceptions.RedundantDataException;
import gateway.GenericGateway;
import model.Plant;
import model.PlantedPlant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantedPlantServiceTest {
    private Plant plant = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "a", 1, 1, 1, 1);
    private PlantedPlant plantedPlant = new PlantedPlant(1L, 1, 1, plant);
    private PlantedPlant plantedPlant2 = new PlantedPlant(1L, 2, 2, plant);

    private PlantedPlantService service;

    @Mock
    private GenericGateway<PlantedPlant> gateway;
    /**
     * this is a mock because it is just a dependency here
     * it will be tested in its own test class
     */
    @Mock
    private PlantService plantService;

    @Before
    public void setUp() throws Exception {
        service = new PlantedPlantService(gateway, plantService);
    }

    @Test
    public void testFindById() {
        when(gateway.findById(1L)).thenReturn(plantedPlant);

        assertEquals("correct result", plantedPlant, service.findById(1L));
    }

    @Test
    public void testFindByAll() {
        List<PlantedPlant> list = Arrays.asList(plantedPlant);

        when(gateway.findAll()).thenReturn(list);

        assertEquals("service calls gateway correctly",list, service.findAll());
    }

    @Test
    public void testSave() {
        when(gateway.save(plantedPlant)).thenReturn(plantedPlant2);

        assertEquals("correct result", plantedPlant2, service.save(plantedPlant));
    }

    @Test(expected = OutOfStockException.class)
    public void testSaveNotEnoughStock() {
        plantedPlant.getPlant().setStockSize(0);
        when(gateway.save(plantedPlant)).thenReturn(plantedPlant2);

        service.save(plantedPlant);
    }

    @Test(expected = RedundantDataException.class)
    public void testSaveInvalid() {
        List<PlantedPlant> list = Arrays.asList(plantedPlant);
        when(gateway.findAll()).thenReturn(list);

        when(gateway.save(plantedPlant)).thenReturn(plantedPlant2);

        service.save(plantedPlant);
    }

    @Test
    public void testUpdate() {
        service.update(1L, plantedPlant);

        verify(gateway).update(1L, plantedPlant);
    }

    @Test(expected = RedundantDataException.class)
    public void testUpdateInvalid() {
        List<PlantedPlant> list = Arrays.asList(plantedPlant);
        when(gateway.findAll()).thenReturn(list);

        service.update(1L, plantedPlant);

        verify(gateway).update(1L, plantedPlant);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(gateway).delete(1L);
    }
}