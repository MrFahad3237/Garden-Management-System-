package TestCode.view;


import View.StandingView;
import View.SearchOption;
import View.StandingController;
import View.StandingModel;
import View.SearchOptionStatus;
import Controller.bill.AuthenticationManager;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
import Controller.service.UserPlantRequestService;
import model.Plant;
import model.User;
import model.UserPlantRequest;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StandingControllerTest {
    @Mock
    private StandingView view;

    private StandingModel model;

    @Mock
    private UserPlantRequestService service;
    @Mock
    private AuthenticationManager manager;
    private StandingController controller;

    @Before
    public void setUp() throws Exception {
        loadData();
        model = new StandingModel();
        controller = new StandingController(view, model, manager, service);
    }

    @Test
    public void testRefreshTableAll() {
        controller.refreshTable();
        long amount = model.getStanding().get("test");
        assertEquals("correct amount shown in table", 3L, amount);
    }

    @Test
    public void testRefreshTableDenied() {
        model.setStatusOption(SearchOptionStatus.DENIED);
        controller.refreshTable();
        long amount = model.getStanding().get("test");

        assertEquals("correct amount shown in table", 6L, amount);
    }

    @Test
    public void testRefreshTableAccepted() {
        model.setStatusOption(SearchOptionStatus.ACCEPTED);
        controller.refreshTable();
        long amount = model.getStanding().get("test");

        assertEquals("correct amount shown in table", 4L, amount);
    }

    @Test
    public void testRefreshTableAcceptedNumberOfRequests() {
        model.setStatusOption(SearchOptionStatus.ACCEPTED);
        model.setOption(SearchOption.NUMBER_OF_REQUESTS);
        controller.refreshTable();
        long amount = model.getStanding().get("test");

        assertEquals("correct amount shown in table", 2L, amount);
    }

    @Test
    public void testUpdate() {
        model.setStatusOption(SearchOptionStatus.ACCEPTED);
        model.setOption(SearchOption.NUMBER_OF_REQUESTS);

        controller.update(new ChannelObservable(), Channel.STANDING, "accept");

        long amount = model.getStanding().get("test");

        assertEquals("correct amount shown in table", 2L, amount);
    }


    private void loadData() {
        UserRole userRole = new UserRole("user", new HashSet<>());
        Plant plant = new Plant("a", 1, 1, 1, 1);
        Plant plant2 = new Plant("b", 2, 2, 2, 2);

        User user = new User("test", "test", userRole);
        UserPlantRequest request1 = new UserPlantRequest(plant, user, 1L);
        UserPlantRequest request2 = new UserPlantRequest(plant, user, 2L);
        UserPlantRequest request3 = new UserPlantRequest(plant2, user, 3L);

        List<UserPlantRequest> findAllRequests = Arrays.asList(request1, request2);
        List<UserPlantRequest> findAcceptedRequests = Arrays.asList(request1, request3);
        List<UserPlantRequest> findDeniedRequests = Arrays.asList(request1, request2, request3);

        when(service.findAllAccepted()).thenReturn(findAcceptedRequests);
        when(service.findAllDenied()).thenReturn(findDeniedRequests);
        when(service.findAll()).thenReturn(findAllRequests);
    }
}