package View;

import Controller.bill.AuthenticationManager;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
import Controller.observer.ChannelObserver;
import Controller.service.UserPlantRequestService;
import lombok.NonNull;
import model.UserPlantRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StandingController implements ChannelObserver {
    static final Logger LOGGER = Logger.getLogger(StandingController.class.getName());

    @NonNull
    private StandingView view;
    @NonNull
    private StandingModel model;

    @NonNull
    private UserPlantRequestService service;

    public StandingController(StandingView view, StandingModel model, AuthenticationManager authenticationManager,
                              UserPlantRequestService service) {
        this.view = view;
        this.model = model;
        this.service = service;

        model.setOption(SearchOption.SUM_OF_REQUESTS);
        model.setStatusOption(SearchOptionStatus.ALL);
        authenticationManager.addNotificationObserver(Channel.STANDING, this);

        view.addOptionButtonListener(new OptionListener());

        refreshTable();
        this.view.addFrame();
    }

    public void refreshTable() {
        loadRequests();
        changeTable();
    }

    private void loadRequests() {
        List<UserPlantRequest> requests;

        switch (model.getStatusOption()) {
            case ACCEPTED:
                requests = service.findAllAccepted();
                break;
            case DENIED:
                requests = service.findAllDenied();
                break;
            case ALL:
                requests = service.findAll();
                break;
            default:
                throw new IllegalArgumentException("Bad type");
        }

        Map<String, Long> standing = new TreeMap<>();

        switch (model.getOption()) {
            case SUM_OF_REQUESTS:
                for (UserPlantRequest request : requests) {
                    String userName = request.getUser().getUsername();
                    if (standing.containsKey(userName)) {
                        standing.put(userName, standing.get(userName) + request.getAmount());
                    } else {
                        standing.put(userName, request.getAmount());
                    }
                }
                break;
            case NUMBER_OF_REQUESTS:
                for (UserPlantRequest request : requests) {
                    String userName = request.getUser().getUsername();
                    if (standing.containsKey(userName)) {
                        standing.put(userName, standing.get(userName) + 1);
                    } else {
                        standing.put(userName, 1L);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Bad type");
        }

        standing = standing.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        model.setStanding(standing);
    }

    public void changeTable() {
        DefaultTableModel table = createTable();
        view.setTable(table);
        view.addFrame();
    }

    private DefaultTableModel createTable() {
        Object[] finalColumnNames = null;

        switch (model.getOption()) {
            case NUMBER_OF_REQUESTS:
                finalColumnNames = new String[]{"userName", "number of requests"};
                break;
            case SUM_OF_REQUESTS:
                finalColumnNames = new String[]{"userName", "total amount"};
                break;
            default:
        }

        Map<String, Long> request = model.getStanding();
        Object rowData[][] = new Object[request.size()][2];

        int index = 0;
        for (Map.Entry<String, Long> entry : request.entrySet()) {
            rowData[index][0] = entry.getKey();
            rowData[index][1] = entry.getValue();
            index++;
        }

        return new DefaultTableModel(rowData, finalColumnNames);
    }

    @Override
    public void update(ChannelObservable o, Channel channnel, Object arg) {
        if (channnel == Channel.STANDING) {
            refreshTable();
        }
    }

    class OptionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object[] a = new Object[4];
            a[0] = "Choose type of standing";
            a[1] = new JComboBox(SearchOption.values());
            a[2] = "Choose type of standing";
            a[3] = new JComboBox(SearchOptionStatus.values());

            if (view.createDialog(a[0], a[1], a[2], a[3])) {
                JComboBox comboBox = (JComboBox) a[1];
                JComboBox comboBox2 = (JComboBox) a[3];
                SearchOption selectedOption = (SearchOption) comboBox.getSelectedItem();
                SearchOptionStatus selectedOption2 = (SearchOptionStatus) comboBox2.getSelectedItem();

                model.setOption(selectedOption);
                model.setStatusOption(selectedOption2);

                refreshTable();
            }
        }
    }
}
