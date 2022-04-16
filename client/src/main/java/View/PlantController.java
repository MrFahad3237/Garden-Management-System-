package View;

import Controller.bill.AuthenticationManager;
import Controller.execptions.BllException;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.RoleService;
import Controller.service.UserPlantRequestService;
import Controller.service.UserService;
import Controller.util.ReportGenerator;
import Controller.validators.IntegerValidatorUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlantController extends AbstractCrudController {
    protected FileChooserView fileChooserView;

    public PlantController(PlantView view, CrudModel model, UserService userService,
                           PlantService plantService, RoleService roleService, PlantedPlantService plantedPlantService,
                           PlotService plotService, UserPlantRequestService requestService,
                           AuthenticationManager authenticationManager, ReportGenerator reportGenerator, FileChooserView fileChooserView) {
        super(view, model, userService, plantService, roleService, plantedPlantService,
                plotService, requestService, authenticationManager, reportGenerator);
        this.fileChooserView = fileChooserView;

        view.addTxtReportButtonListener(new AddReportTxtListener());
        view.addPdfReportButtonListener(new AddReportPdfListener());
        refreshTable();

    }

    public void update() {
        JTable m = view.getTable();
        int row = m.getSelectedRow();
        if (row >= 0) {//only if something is selected
            Long id = Long.parseLong((String) view.getTableModel().getValueAt(row, 0));
            constructPlantFromDialogPane(id, Operation.UPDATE);
        }

    }

    @Override
    public void insert() {
        constructPlantFromDialogPane(null, Operation.INSERT);
        refreshTable();
    }

    @Override
    public void delete() {
        JTable m = view.getTable();
        int row = m.getSelectedRow();
        if (row >= 0) {//only if something is selected
            Long id = Long.parseLong((String) view.getTableModel().getValueAt(row, 0));
            deletePlant(id);
            refreshTable();
        }
    }

    @Override
    protected boolean isRestrictedField(String name) {
        return "plantedPlants".equals(name);
    }

    @Override
    public void refreshTable() {
        changeTable(getPlants());
    }

    private void constructPlantFromDialogPane(Long id, Operation op) {
        Object[] a = new Object[11];
        a[0] = "Id : generated";
        a[1] = "Type";
        a[3] = "Average Life";
        a[5] = "Water Requirements";
        a[7] = "Plot Dimension";
        a[9] = "Plant stock size";

        switch (op) {
            case INSERT:
                a[2] = new JTextField();
                a[4] = new JTextField();
                a[6] = new JTextField();
                a[8] = new JTextField();
                a[10] = new JTextField();
                break;
            case UPDATE:
                a[2] = new JTextField(getPlantTypeById(id));
                a[4] = new JTextField(getPlantAverageLifeById(id) + "");
                a[6] = new JTextField(getPlantWaterRequirements(id) + "");
                JTextField jTextField = new JTextField(getPlantPlotSize(id) + "");
                jTextField.setEditable(false);
                a[8] = jTextField;
                a[10] = new JTextField(getPlantStockSize(id) + "");
                break;
            default:
                break;
        }

        if (view.createDialog(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8],a[9], a[10])) {
            JTextField f1 = (JTextField) a[2];
            JTextField f2 = (JTextField) a[4];
            JTextField f3 = (JTextField) a[6];
            JTextField f4 = (JTextField) a[8];
            JTextField f5 = (JTextField) a[10];

            try {
                if (IntegerValidatorUtil.validateString(f2.getText())
                        && IntegerValidatorUtil.validateString(f3.getText())
                        && IntegerValidatorUtil.validateString(f4.getText())
                        && IntegerValidatorUtil.validateString(f5.getText())) {
                    switch (op) {
                        case INSERT:
                            addPlant(f1.getText(),
                                    Integer.parseInt(f2.getText()),
                                    Integer.parseInt(f3.getText()),
                                    Integer.parseInt(f4.getText()),
                                    Integer.parseInt(f5.getText()));
                            break;
                        case UPDATE:
                            updatePlant(id, f1.getText(),
                                    Integer.parseInt(f2.getText()),
                                    Integer.parseInt(f3.getText()),
                                    Integer.parseInt(f4.getText()),
                                    Integer.parseInt(f5.getText()));
                            break;
                        default:
                            break;
                    }
                }
            } catch (BllException e) {
                view.showMessage(e.getMessage(), JOptionPane.WARNING_MESSAGE);
            }

            refreshTable();
        }
    }

    class AddReportTxtListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String path = fileChooserView.getSelectedPath();
            if (path != null) {
                generateReportTxt(path);
            }
        }
    }

    class AddReportPdfListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String path = fileChooserView.getSelectedPath();
            if (path != null) {
                generateReportPdf(path);
            }
        }
    }
}
