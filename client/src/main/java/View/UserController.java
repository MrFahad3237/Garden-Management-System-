package View;

import Controller.bill.AuthenticationManager;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.RoleService;
import Controller.service.UserPlantRequestService;
import Controller.service.UserService;
import Controller.util.ReportGenerator;
import model.UserPlantRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserController extends AbstractCrudController {

    public UserController(UserView view, CrudModel model, UserService userService,
                          PlantService plantService, RoleService roleService,
                          PlantedPlantService plantedPlantService, PlotService plotService,
                          UserPlantRequestService requestService,
                          AuthenticationManager authenticationManager, ReportGenerator reportGenerator) {
        super(view, model, userService, plantService, roleService, plantedPlantService, plotService,
                requestService,
                authenticationManager,
                reportGenerator);

        view.addPlantButtonListener(new AddPlantListener());
        view.addGardenButtonListener(new AddGardenListener());
        view.addRequestButtonListener(new AddRequestListener());
        view.addStandingButtonListener(new AddStandingListener());
        refreshTable();
    }

    public void update() {
        JTable m = view.getTable();
        int row = m.getSelectedRow();
        if (row >= 0) {//only if something is selected
            Long id = Long.parseLong((String) view.getTableModel().getValueAt(row, 0));
            constructUserFromDialogPane(id, Operation.UPDATE);
        }

    }

    @Override
    public void insert() {
        constructUserFromDialogPane(null, Operation.INSERT);
        refreshTable();
    }

    @Override
    public void delete() {
        JTable m = view.getTable();
        int row = m.getSelectedRow();
        if (row >= 0) {//only if something is selected
            Long id = Long.parseLong((String) view.getTableModel().getValueAt(row, 0));
            deleteUser(id);
            refreshTable();
        }
    }

    @Override
    public void refreshTable() {
        changeTable(getRegularUsers());
    }

    @Override
    protected void closeWindow() {
        authenticationManager.logout();
        super.closeWindow();
        System.exit(0);
    }

    private void constructUserFromDialogPane(Long id, Operation op) {
        Object[] a = new Object[6];
        a[0] = "Id : generated";
        a[1] = "Username";
        a[4] = new JTextField();

        switch (op) {
            case INSERT:
                a[3] = "Password";
                a[2] = new JTextField();
                break;
            case UPDATE:
                a[3] = "New Password";
                a[2] = new JTextField(getUsernameById(id));
                a[5] = new JCheckBox("Change password");
                break;
            default:
                break;
        }

        if (view.createDialog(a[0], a[1], a[2], a[3], a[4], a[5])) {
            JTextField f1 = (JTextField) a[2];
            JTextField f2 = (JTextField) a[4];

            switch (op) {
                case INSERT:
                    addUser(f1.getText(), f2.getText());
                    break;
                case UPDATE:
                    boolean changePassword = ((JCheckBox) a[5]).isSelected();
                    updateUser(id, f1.getText(), f2.getText(), changePassword);
                    break;
                default:
                    break;
            }

            refreshTable();
        }
    }

    class AddPlantListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //show GUI for plants
            PlantView plantView = new PlantViewImpl();
            new PlantController(plantView, model,
                    userService, plantService, roleService, plantedPlantService, plotService,
                    requestService,
                    authenticationManager, reportGenerator, new FileChooserViewImpl());
        }
    }

    class AddGardenListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            GardenViewImpl gardenView = new GardenViewImpl();
            GardenModel gardenModel = new GardenModel();

            new GardenController(gardenView, gardenModel,
                    plantService, plantedPlantService,
                    plotService, requestService,
                    authenticationManager);
        }
    }

    class AddRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<UserPlantRequest> requests = requestService.findNotAccepted();

            if (requests.isEmpty()) {
                view.showMessage("No more requests!", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Object[] a = new Object[requests.size() * 3];

            JPanel panel = new JPanel(new GridLayout(0,1));
            panel.setSize(600, 400);


            for (int i = 0; i < requests.size(); i++) {
                a[3 * i] = requests.get(i);
                JRadioButton checkboxAccept = new JRadioButton("Accept");
                JRadioButton checkboxDeny = new JRadioButton("Deny");

                a[3 * i + 1] = checkboxAccept;
                a[3 * i + 2] = checkboxDeny;
                ButtonGroup group = new ButtonGroup();
                group.add(checkboxAccept);
                group.add(checkboxDeny);

                panel.add(new JLabel(a[3 * i].toString()));
                panel.add((JComponent) a[3 * i + 1]);
                panel.add((JComponent) a[3 * i + 2]);
            }

            JScrollPane scroll = new JScrollPane(panel);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scroll.setPreferredSize(new Dimension(600, 400));

            if (view.createDialog(scroll)) {
                for (int i = 0; i < requests.size(); i++) {
                    JRadioButton checkboxAccept = (JRadioButton) a[3 * i + 1];
                    JRadioButton checkboxDeny = (JRadioButton) a[3 * i + 2];

                    if (checkboxAccept.isSelected()) {
                        requestService.acceptRequest(requests.get(i).getId());
                    } else if (checkboxDeny.isSelected()) {
                        requestService.denyRequest(requests.get(i).getId());
                    }
                }
            }
        }
    }

    class AddStandingListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            StandingModel model = new StandingModel();
            new StandingController(new StandingViewImpl(), model, authenticationManager
                    , requestService);
        }
    }
}



