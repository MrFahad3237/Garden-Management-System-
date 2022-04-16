package TestCode.view;


import View.PlantView;
import View.CrudModel;
import View.PlantController;
import View.FileChooserView;
import Controller.bill.AuthenticationManager;
import Controller.execptions.DuplicateEntityException;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.RoleService;
import Controller.service.UserPlantRequestService;
import Controller.service.UserService;
import Controller.util.FileMaker;
import Controller.util.ReportGenerator;
import Controller.util.ReportType;
import model.Garden;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantControllerTest {

    @Mock
    private PlantView view;
    @Mock
    private FileChooserView fileChooserView;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private PlantService plantService;
    @Mock
    private RoleService roleService;
    @Mock
    private PlantedPlantService plantedPlantService;
    @Mock
    private PlotService plotService;
    @Mock
    private UserPlantRequestService requestService;
    @Mock
    private ReportGenerator reportGenerator;

    private PlantController controller;

    private CrudModel model;

    @Before
    public void setUp() {
        loadGarden();
        model = new CrudModel(new Garden(new HashSet<>(), new HashSet<>()), new HashSet<>(), new HashSet<>());
        controller = new PlantController(view, model, userService,
                plantService, roleService, plantedPlantService,
                plotService, requestService, authenticationManager, reportGenerator, fileChooserView);
    }

    @Test
    public void testUpdate() {
        final Long indexToUpdate = 1L;
        final String type = "newType";
        final int avgLife = 1;
        final int waterRequirement = 1;
        final int plotSize = 1;

        JTable jTable = mock(JTable.class);
        when(jTable.getSelectedRow()).thenReturn(1);
        when(view.getTable()).thenReturn(jTable);

        DefaultTableModel tableModel = mock(DefaultTableModel.class);
        when(view.getTableModel()).thenReturn(tableModel);
        when(tableModel.getValueAt(1, 0)).thenReturn(indexToUpdate.toString());

        when(plantService.findById(indexToUpdate)).thenReturn(new Plant(indexToUpdate, type,
                avgLife, waterRequirement, plotSize, 1, null));

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            ((JTextField) arg[2]).setText(type);
            ((JTextField) arg[4]).setText(String.valueOf(avgLife));
            ((JTextField) arg[6]).setText(String.valueOf(waterRequirement));
            ((JTextField) arg[8]).setText(String.valueOf(plotSize));
            return true;
        }).when(view).createDialog(anyVararg());

        controller.update();

        assertTrue("plant updated in model", model.getPlants().stream().
                anyMatch(plant -> plant.getType().equals(type) && plant.getId().equals(indexToUpdate)));
    }

    @Test
    public void testInsert() {
        when(plantService.save(any(Plant.class))).thenReturn(new Plant(10L, "newType", 1, 1, 1, 1, null));

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            ((JTextField) arg[2]).setText("newType");
            ((JTextField) arg[4]).setText("1");
            ((JTextField) arg[6]).setText("1");
            ((JTextField) arg[8]).setText("1");
            ((JTextField) arg[10]).setText("1");
            return true;
        }).when(view).createDialog(anyVararg());

        controller.insert();

        assertTrue("plant was added to model", model.getPlants().stream().anyMatch(plant -> plant.getType().equals("newType")));
    }

    @Test
    public void testInsertInvalidNumber() {
        when(plantService.save(any(Plant.class))).thenReturn(new Plant(10L, "newType", 1, 1, 1, 1, null));

        LogManager.getLogManager().reset();//disable logging for this test

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            ((JTextField) arg[2]).setText("newType");
            ((JTextField) arg[4]).setText("invalidNumber");//invalid number is inserted into the dialog box
            ((JTextField) arg[6]).setText("1");
            ((JTextField) arg[8]).setText("1");
            return true;
        }).when(view).createDialog(anyVararg());

        controller.insert();

        verify(view).showMessage("Invalid Number", JOptionPane.WARNING_MESSAGE);
        assertFalse("plant was not added to model", model.getPlants().stream().anyMatch(plant -> plant.getType().equals("newType")));
    }

    @Test
    public void testDelete() {
        final int indexToChoose = 1;

        JTable jTable = mock(JTable.class);
        when(jTable.getSelectedRow()).thenReturn(indexToChoose);
        when(view.getTable()).thenReturn(jTable);

        DefaultTableModel tableModel = mock(DefaultTableModel.class);
        when(view.getTableModel()).thenReturn(tableModel);
        when(tableModel.getValueAt(1, 0)).thenReturn("1");

        controller.delete();

        assertFalse("plant was deleted from model", model.getPlants().stream().anyMatch(plant -> plant.getId().equals(1L)));
    }



    @Test
    public void testRefreshTable() {
        model.addPlant(new Plant(10L, "refreshType", 1, 1, 1, 1, null));
        List[] result = new Vector[2];

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            DefaultTableModel tableModel = (DefaultTableModel) arg[0];
            result[1] = tableModel.getDataVector();
            return true;
        }).when(view).setTable(any(DefaultTableModel.class));

        controller.refreshTable();

        assertTrue("view was updated with the new plant", result[1].stream().
                anyMatch(vector -> ((List) vector).stream().
                        anyMatch(plant -> "refreshType".equals(plant))));
    }

    @Test
    public void testDeletePlant() {
        controller.deletePlant(1L);

        assertFalse("plant was deleted", model.getPlants().stream().anyMatch(t -> t.getId().equals(1L)));
        verify(plantService).delete(1L);
    }

    @Test
    public void testGetPlantTypeById() {
        when(plantService.findById(1L)).thenReturn(new Plant("test", 1, 1, 1, 1));
        assertEquals("plant type is test", "test", controller.getPlantTypeById(1L));
    }

    @Test
    public void testGetPlantAverageLifeById() {
        when(plantService.findById(1L)).thenReturn(new Plant("test", 1, 1, 1, 1));
        assertEquals("avg. life of 1", 1, controller.getPlantAverageLifeById(1L));
    }

    @Test
    public void testGetPlantWaterRequirements() {
        when(plantService.findById(1L)).thenReturn(new Plant("test", 1, 1, 1, 1));
        assertEquals("water req. of 1", 1, controller.getPlantWaterRequirements(1L));
    }

    @Test
    public void testGetPlantPlotSize() {
        when(plantService.findById(1L)).thenReturn(new Plant("test", 1, 1, 1, 1));
        assertEquals("plot size of 1", 1, controller.getPlantPlotSize(1L));
    }

    @Test
    public void testAddPlant() {
        final String plantType = "aaa";

        when(plantService.save(any(Plant.class))).thenReturn(
                new Plant(10L, plantType, 1, 1, 1, 1, new ArrayList<>()));

        controller.addPlant(plantType, 1, 1, 1, 1);

        assertTrue("plant was added", model.getPlants().stream().anyMatch(t -> t.getId().equals(10L)));
        verify(plantService).save(any(Plant.class));
    }

    @Test
    public void testAddPlantEmpty() {
        final String plantType = "";

        controller.addPlant(plantType, 1, 1, 1, 1);

        assertFalse("plant was added", model.getPlants().stream().anyMatch(t -> t.getId().equals(10L)));
        verify(plantService, never()).save(any(Plant.class));
        verify(view).showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
    }

    @Test
    public void testAddPlantDuplicate() {
        when(plantService.save(any(Plant.class))).thenThrow(new DuplicateEntityException("Duplicate plant"));

        controller.addPlant("f", 1, 1, 1, 1);

        assertFalse("cannot add duplicate", model.getPlants().stream().anyMatch(t -> t.getType().equals("f")));
        verify(plantService).save(any(Plant.class));
    }

    @Test
    public void testUpdatePlant() {
        final String plantType = "aaa";

        controller.updatePlant(1L, plantType, 1, 1, 1, 1);

        assertTrue("plant with new type is present in the model", model.getPlants().stream().anyMatch(t -> t.getId().equals(1L) &&
                t.getType().equals(plantType)));
        verify(plantService).update(anyLong(), any(Plant.class));
    }

    @Test
    public void testUpdatePlantEmpty() {
        final String plantType = "";

        controller.updatePlant(1L, plantType, 1, 1, 1, 1);

        assertFalse("plant with new type is present in the model", model.getPlants().stream().anyMatch(t -> t.getId().equals(1L) &&
                t.getType().equals(plantType)));
        verify(plantService, never()).update(anyLong(), any(Plant.class));
        verify(view).showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
    }

    @Test
    public void testUpdatePlantDuplicate() {
        doThrow(new DuplicateEntityException("duplicate")).when(plantService).update(anyLong(), any(Plant.class));

        controller.updatePlant(1L, "aaa", 1, 1, 1, 1);

        assertFalse("cannot update duplicate", model.getPlants().stream().anyMatch(t -> t.getId().equals(1L) && t.getType().equals("aaa")));
        verify(plantService).update(anyLong(), any(Plant.class));
    }

    @Test
    public void testGenerateReportPdf() {
        FileMaker maker = mock(FileMaker.class);
        when(reportGenerator.generateReport(any())).thenReturn(maker);

        controller.generateReportPdf("/file");

        verify(reportGenerator).generateReport(ReportType.PDF);
    }




    @Test
    public void testGetPlants() {
        assertEquals("set up was done with 5 plants", 5, controller.getPlants().size());
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