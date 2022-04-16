package bll.service;

import bll.exceptions.DuplicateEntityException;
import gateway.GenericGateway;
import model.Plant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantServiceTest {
    private Plant plant = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "a", 1, 1, 1, 1);
    private Plant plant2 = new Plant(1L, new ArrayList<>(), new ArrayList<>(), "b", 1, 1, 1, 1);

    @Mock
    private GenericGateway<Plant> gateway;

    private PlantService service;

    @Before
    public void setUp() throws Exception {
        service = new PlantService(gateway);
    }

    @Test
    public void testSave() {
        when(gateway.save(plant)).thenReturn(plant2);

        assertEquals("correct result", plant2, service.save(plant));
    }

    @Test(expected = DuplicateEntityException.class)
    public void testSaveDuplicate() {
        List<Plant> list = Arrays.asList(plant);

        when(gateway.save(plant)).thenReturn(plant2);
        when(gateway.findAll()).thenReturn(list);

        service.save(plant);
    }
}