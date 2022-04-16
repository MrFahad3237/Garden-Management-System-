package TestCode.view;


import View.CrudModel;
import View.UserController;
import View.UserView;
import Controller.bill.AuthenticationManager;
import Controller.execptions.DuplicateEntityException;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
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
import model.User;
import model.UserPlantRequest;
import model.UserRole;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private UserView view;
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
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private UserController controller;

    private CrudModel model;

    private UserRole userRole;

    @Before
    public void setUp() {
        userRole = new UserRole("user", new HashSet<>());
        loadGarden();

        model = new CrudModel(new Garden(new HashSet<>(), new HashSet<>()), new HashSet<>(), new HashSet<>());
        controller = new UserController(view, model, userService,
                plantService, roleService, plantedPlantService,
                plotService, requestService, authenticationManager, reportGenerator);
    }

    @Test
    public void testLoadGardenUser() {
        verify(userService, times(1)).findRegularUsers();
        assertEquals("2 users loaded", 2, model.getRegularUsers().size());
    }

    @Test
    public void testLoadGardenPlot() {
        verify(plotService, times(1)).findAll();
        assertEquals("2 plots loaded", 2, model.getGarden().getPlots().size());
    }

    @Test
    public void testLoadGardenPlant() {
        verify(plantService, times(1)).findAll();
        assertEquals("6 plants loaded", 5, model.getPlants().size());
    }

    @Test
    public void testLoadGardenPlantedPlant() {
        verify(plantedPlantService, times(1)).findAll();
        assertEquals("3 plantedplants loaded", 3, model.getGarden().getPlantedPlants().size());
    }

    @Test
    public void testLoadGardenRole() {
        verify(roleService, times(1)).findByName("user");

        assertEquals("userrole loaded", "user", model.getRegularRole().getName());
    }

    @Test
    public void testChangeTable() {

        controller.changeTable(new ArrayList<>(model.getRegularUsers()));

        //these methods are also called
        verify(view, atLeast(1)).setTable(any());
        verify(view, atLeast(1)).addFrame();
    }


    @Test
    public void testAddUser() {
        when(userService.save(any(User.class))).thenReturn(
                new User("test", "encoded", userRole));

        controller.addUser("test", "test");

        assertTrue("new user was added", model.getRegularUsers().stream().anyMatch(t -> t.getUsername().equals("test")
                && t.getPassword().equals("encoded")));
        verify(userService, times(1)).save(any(User.class));
    }


    @Test
    public void testAddDuplicateUser() {
        when(userService.save(any(User.class))).thenThrow(new DuplicateEntityException("duplicate"));

        controller.addUser("user2", "user2");

        assertEquals("duplicated user was not added", 1, model.getRegularUsers().stream().filter(t -> t.getUsername().equals("user2"))
                .collect(Collectors.toList()).size());
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    public void testGetUsernameById() {
        when(userService.findById(1L)).thenReturn(new User("a", "a", userRole));
        assertEquals("userName is a", "a", controller.getUsernameById(1L));
    }


    @Test
    public void testGenerateReportTxt() {
        FileMaker maker = mock(FileMaker.class);
        when(reportGenerator.generateReport(any())).thenReturn(maker);

        controller.generateReportTxt("/file");

        verify(reportGenerator).generateReport(ReportType.TXT);
    }

    @Test
    public void testGenerateReportPdf() {
        FileMaker maker = mock(FileMaker.class);
        when(reportGenerator.generateReport(any())).thenReturn(maker);

        controller.generateReportPdf("/file");

        verify(reportGenerator).generateReport(ReportType.PDF);
    }

    @Test
    public void testUpdate() {
        final Long userId = 1L;
        final String newUsername = "update";
        final String newPassword = "update";

        JTable jTable = mock(JTable.class);
        when(jTable.getSelectedRow()).thenReturn(1);
        when(view.getTable()).thenReturn(jTable);

        DefaultTableModel tableModel = mock(DefaultTableModel.class);
        when(view.getTableModel()).thenReturn(tableModel);
        when(tableModel.getValueAt(1, 0)).thenReturn(userId.toString());

        User user = new User();
        user.setUsername("user");

        when(userService.findById(userId)).thenReturn(user);
        when(userService.update(eq(userId), any(), anyBoolean())).thenReturn(new User(userId, newUsername,
                newPassword, userRole));

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            ((JTextField) arg[2]).setText(newUsername);
            ((JTextField) arg[4]).setText(newPassword);
            ((JCheckBox) arg[5]).setSelected(true);
            return true;
        }).when(view).createDialog(anyVararg());

        controller.update();

        verify(userService).update(eq(1L), any(), anyBoolean());
        assertTrue("user was updated", model.getRegularUsers().stream().anyMatch(t -> t.getUsername().
                equals(newUsername) && t.getPassword().equals(newPassword)));
    }


    @Test
    public void testDelete() {
        JTable jTable = mock(JTable.class);
        when(jTable.getSelectedRow()).thenReturn(1);
        when(view.getTable()).thenReturn(jTable);

        DefaultTableModel tableModel = mock(DefaultTableModel.class);
        when(view.getTableModel()).thenReturn(tableModel);
        when(tableModel.getValueAt(1, 0)).thenReturn("1");

        controller.delete();

        assertFalse("user was deleted", model.getRegularUsers().stream().anyMatch(t -> t.getId().equals(1L)));
        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    public void testRefreshTable() {
        model.addUser(new User(10L, "refreshName", "b", userRole));
        List[] result = new Vector[2];//use array to overcome lambda expression violation

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            DefaultTableModel tableModel = (DefaultTableModel) arg[0];
            result[1] = tableModel.getDataVector();

            return true;
        }).when(view).setTable(any(DefaultTableModel.class));

        controller.refreshTable();

        assertTrue("view was updated with the new user", result[1].stream().
                anyMatch(vector -> ((List) vector).stream().
                        anyMatch(t -> "refreshName".equals(t))));
    }



    @Test
    public void testUpdateDataChange() {
        controller.update(new ChannelObservable(), Channel.LOGIN, "login#user1");

        ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
        //verify that tooltip was shown with live notification
        verify(view).showTooltip(msg.capture());
        assertTrue("msg contains the username", msg.getValue().contains("user1"));
    }

    private void loadGarden() {
        User user = new User("user", "user", userRole);
        User user2 = new User("user2", "user2", userRole);
        user.setId(1L);
        user2.setId(2L);

        Plot plot1 = new Plot(6, 4, 0, 0);
        Plot plot2 = new Plot(4, 2, 10, 10);
        plot1.setId(1L);
        plot2.setId(2L);

        Plant plant = new Plant("a", 1, 1, 1, 1);
        Plant plant2 = new Plant("b", 1, 1, 1, 5);
        Plant plant3 = new Plant("c", 1, 1, 2, 1);
        Plant plant4 = new Plant("d", 1, 1, 3, 0);
        Plant plant5 = new Plant("e", 1, 1, 4, 2);
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

        when(userService.findRegularUsers()).thenReturn(Arrays.asList(user, user2));
        when(plotService.findAll()).thenReturn(Arrays.asList(plot1, plot2));
        when(plantService.findAll()).thenReturn(Arrays.asList(plant, plant2, plant3, plant4, plant5));
        when(plantedPlantService.findAll()).thenReturn(Arrays.asList(plantedPlant,
                plantedPlant2, plantedPlant3));
        when(roleService.findByName("user")).thenReturn(userRole);
    }

    private void setUpInsertUser(String userName, String password) {
        when(userService.save(any())).thenReturn(new User(10L, userName, password, userRole));

        doAnswer(invocation -> {
            Object[] arg = invocation.getArguments();
            ((JTextField) arg[2]).setText(userName);
            ((JTextField) arg[4]).setText(password);
            return true;
        }).when(view).createDialog(anyVararg());
    }
}