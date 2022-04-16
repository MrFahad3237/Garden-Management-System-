package TestCode.view;


import View.GardenController;
import View.GardenModel;
import View.IndexButton;
import View.GardenView;
import Controller.bill.AuthenticationManager;
import Controller.execptions.RedundantDataException;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.UserPlantRequestService;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import model.UserPlantRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GardenControllerTest {

    @Mock
    private GardenView view;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AuthenticationManager authenticationManager;
    @Mock
    private PlantService plantService;
    @Mock
    private PlantedPlantService plantedPlantService;
    @Mock
    private PlotService plotService;
    @Mock
    private UserPlantRequestService requestService;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private GardenController controller;

    private GardenModel model;

    @Before
    public void setUp() {
        model = new GardenModel();
        loadGarden();
        controller = new GardenController(view, model,
                plantService, plantedPlantService,
                plotService, requestService, authenticationManager);
    }

    @Test
    public void testUpdateGridsCheckId() {
        PlantedPlant firstPlantedPlant = new ArrayList<>(model.getGarden().getPlantedPlants()).get(0);
        final int posX = firstPlantedPlant.getX();
        final int posY = firstPlantedPlant.getY();

        controller.updateGrids();

        ArgumentCaptor<String> ids = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).addLabel(eq(posX), eq(posY), ids.capture(), any());

        assertTrue("first planted plant was inserted into the view", ids.getAllValues().
                contains(String.valueOf(firstPlantedPlant.getId())));
    }

    @Test
    public void testUpdateGridsCheckType() {
        PlantedPlant firstPlantedPlant = new ArrayList<>(model.getGarden().getPlantedPlants()).get(0);
        final int posX = firstPlantedPlant.getX();
        final int posY = firstPlantedPlant.getY();

        controller.updateGrids();

        ArgumentCaptor<String> types = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).addLabel(eq(posX), eq(posY), any(), types.capture());

        assertTrue("first planted plant was inserted into the view",
                types.getAllValues().contains(firstPlantedPlant.getPlant().getType()));
    }

    @Test
    public void testShowPlantDialog() {
        final int chosenPlantIndex = 3;
        final Long[] chosenPlantId = new Long[1];//to overcome the effect of the lambda expression

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            JComboBox box = (JComboBox) arg[1];
            box.setSelectedIndex(chosenPlantIndex);
            chosenPlantId[0] = ((Plant) box.getItemAt(chosenPlantIndex)).getId();
            return true;
        }).when(view).createDialog(anyVararg());

        controller.showPlantDialog();

        assertTrue("new chosenPlantId after showing dialog", model.getChosenPlantId().equals(chosenPlantId[0]));
    }

    @Test
    public void testLoadGardenCheckPlant() {
        //no need to call loadGarden() because it is called during initialization anyway

        assertTrue("plant is planted at 0,0 - see initialization", model.getGardenRepr()[0][0][0].equals("plant"));
    }

    @Test
    public void testLoadGardenCheckId() {
        //no need to call loadGarden() because it is called during initialization anyway

        assertTrue("plant with id '1' is planted at 0,0 - see initialization", model.getGardenRepr()[0][0][1].equals("1"));
    }

    @Test
    public void testLoadGardenCheckType() {
        //no need to call loadGarden() because it is called during initialization anyway

        assertTrue("plant with type 'a' is planted at 0,0 - see initialization", model.getGardenRepr()[0][0][2].equals("a"));
    }

    @Test
    public void testPlantAtPlot() {
        final int plantAtX = 10;
        final int plantAtY = 10;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        PlantedPlant plantedPlantToAdd = new PlantedPlant(10L, plantAtX, plantAtY, model.getPlants().get(0));
        when(plantedPlantService.save(any())).thenReturn(plantedPlantToAdd);
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        controller.plantAt(plantAtX, plantAtY);

        assertTrue("can plant in this location", model.getGarden().getPlantedPlants().contains(plantedPlantToAdd));
    }

    @Test
    public void testPlantAtNoPlot() {
        final int plantAtX = 5;
        final int plantAtY = 5;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        PlantedPlant plantedPlantToAdd = new PlantedPlant(10L, plantAtX, plantAtY, model.getPlants().get(0));
        when(plantedPlantService.save(any())).thenReturn(plantedPlantToAdd);
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        controller.plantAt(plantAtX, plantAtY);

        assertFalse("no plot found in this position", model.getGarden().getPlantedPlants().contains(plantedPlantToAdd));
    }

    @Test
    public void testPlantAtNotEnoughSpace() {
        final int plantAtX = 0;
        final int plantAtY = 0;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        PlantedPlant plantedPlantToAdd = new PlantedPlant(10L, plantAtX, plantAtY, model.getPlants().get(0));
        when(plantedPlantService.save(any())).thenReturn(plantedPlantToAdd);
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        controller.plantAt(plantAtX, plantAtY);

        assertFalse("plot is not empty", model.getGarden().getPlantedPlants().contains(plantedPlantToAdd));
    }

    @Test
    public void testPlantAtBiggerThanHeight() {
        final int plantAtX = 0;
        final int plantAtY = model.getGarden().getHeight() + 1;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        PlantedPlant plantedPlantToAdd = new PlantedPlant(10L, plantAtX, plantAtY, model.getPlants().get(0));
        when(plantedPlantService.save(any())).thenReturn(plantedPlantToAdd);
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        controller.plantAt(plantAtX, plantAtY);

        assertFalse("position is bigger than height of the garden", model.getGarden().getPlantedPlants().contains(plantedPlantToAdd));
    }

    @Test
    public void testPlantAtBiggerThanWidth() {
        final int plantAtX = model.getGarden().getWidth() + 1;
        final int plantAtY = 0;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        PlantedPlant plantedPlantToAdd = new PlantedPlant(10L, plantAtX, plantAtY, model.getPlants().get(0));
        when(plantedPlantService.save(any())).thenReturn(plantedPlantToAdd);
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        controller.plantAt(plantAtX, plantAtY);

        assertFalse("position is bigger than width of the garden", model.getGarden().getPlantedPlants().contains(plantedPlantToAdd));
    }

    @Test
    public void testPlantRedundantDataException() {
        final int plantAtX = 10;
        final int plantAtY = 10;
        final Long chosenPlantId = 1L;
        model.setChosenPlantId(chosenPlantId);

        when(plantedPlantService.save(any())).thenThrow(new RedundantDataException("redundant data"));
        when(plantService.findById(chosenPlantId)).thenReturn(model.getPlants().get(0));

        assertFalse("cannot place plant", controller.plantAt(plantAtX, plantAtY));
        verify(view).showMessage(anyString(), anyInt());
    }

    @Test
    public void testUpdateDataChange() {
        controller.update(new ChannelObservable(), Channel.DATA_CHANGE, "change");

        //verify if data was refreshed from the services
        verify(plantedPlantService, atLeastOnce()).findAll();
    }

    @Test
    public void testUpdatePlantAt() {
        final int plantAtX = 5;//note the reverse order
        final int plantAtY = 1;
        final String notifyUsername = "userName";

        controller.update(new ChannelObservable(), Channel.PLANT_AT, "plantAt#" + plantAtY + "#" +
                plantAtX + "#" + notifyUsername);

        //verify if data was refreshed from the services
        verify(view).showTooltip(notifyUsername, plantAtX, plantAtY);
    }



    private void loadGarden() {
        Plot plot1 = new Plot(6, 4, 0, 0);
        Plot plot2 = new Plot(4, 2, 10, 10);
        plot1.setId(1L);
        plot2.setId(2L);

        Plant plant = new Plant("a", 1, 1, 1, 1);
        Plant plant2 = new Plant("b", 1, 1, 1, 1);
        Plant plant3 = new Plant("c", 1, 1, 2, 1);
        Plant plant4 = new Plant("d", 1, 1, 3, 1);
        Plant plant5 = new Plant("e", 1, 1, 4, 1);
        plant.setId(1L);
        plant2.setId(2L);
        plant3.setId(3L);
        plant4.setId(4L);
        plant5.setId(5L);

        PlantedPlant plantedPlant = new PlantedPlant(0, 0, plant);
        PlantedPlant plantedPlant2 = new PlantedPlant(2, 2, plant2);
        PlantedPlant plantedPlant3 = new PlantedPlant(3, 3, plant2);
        plantedPlant.setId(1L);
        plantedPlant2.setId(2L);
        plantedPlant3.setId(3L);

        when(plotService.findAll()).thenReturn(Arrays.asList(plot1, plot2));
        when(plantService.findAll()).thenReturn(Arrays.asList(plant, plant2, plant3, plant4, plant5));
        when(plantedPlantService.findAll()).thenReturn(Arrays.asList(plantedPlant,
                plantedPlant2, plantedPlant3));
    }
}