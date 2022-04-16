package View;

import Controller.bill.AuthenticationManager;
import Controller.execptions.BllException;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
import Controller.observer.ChannelObserver;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.RoleService;
import Controller.service.UserPlantRequestService;
import Controller.service.UserService;
import Controller.util.ReportGenerator;
import Controller.util.ReportType;
import model.Garden;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isStatic;
import static org.hibernate.internal.util.StringHelper.isEmpty;

public abstract class AbstractCrudController implements ChannelObserver {
    static final Logger LOGGER = Logger.getLogger(AbstractCrudController.class.getName());
    protected CrudView view;
    protected CrudModel model;
    protected UserService userService;
    protected PlantService plantService;
    protected RoleService roleService;
    protected PlantedPlantService plantedPlantService;
    protected PlotService plotService;
    protected UserPlantRequestService requestService;
    protected AuthenticationManager authenticationManager;
    protected ReportGenerator reportGenerator;

    public AbstractCrudController(CrudView view, CrudModel model, UserService userService,
                                  PlantService plantService, RoleService roleService,
                                  PlantedPlantService plantedPlantService, PlotService plotService,
                                  UserPlantRequestService requestService,
                                  AuthenticationManager authenticationManager, ReportGenerator reportGenerator) {
        this.view = view;
        this.model = model;
        this.userService = userService;
        this.plantService = plantService;
        this.roleService = roleService;
        this.plantedPlantService = plantedPlantService;
        this.plotService = plotService;
        this.requestService = requestService;
        this.authenticationManager = authenticationManager;
        this.reportGenerator = reportGenerator;
        this.view.addUpdateButtonListener(new UpdateListener());
        this.view.addInsertButtonListener(new InsertListener());
        this.view.addDeleteButtonListener(new DeleteListener());
        this.view.addWindowClosingListener(new WindowAdapterListener());

        authenticationManager.addNotificationObserver(Channel.LOGIN, this);

        loadGarden();

        this.view.addFrame();
    }

    public void loadGarden() {
        model.setRegularUsers(new HashSet<>(userService.findRegularUsers()));
        List<Plot> plots = plotService.findAll();
        model.setPlants(new HashSet<>(plantService.findAll()));
        List<PlantedPlant> plantedPlants = plantedPlantService.findAll();

        model.setGarden(new Garden(new HashSet<>(plots), new HashSet<>(plantedPlants)));

        model.setRegularRole(roleService.findByName("user"));
    }

    public void changeTable(List<?> objects) {
        DefaultTableModel table = createTable(objects);
        view.setTable(table);
        view.addFrame();
    }

    //populating the table is done with reflection so some fields need to be changed manually
    protected String getValueColumn(String column, Object defaultValue) {
        return defaultValue.toString();
    }

    protected boolean isCellEditableTable(int row, int column) {
        return false;//table is not editable by default
    }

    /**
     * Uses reflection to generate column names for the table
     */
    protected DefaultTableModel createTable(List<?> objects) {
        if (!objects.isEmpty()) {

            //do not include static fields
            List<Field> fields = Arrays.stream(objects.get(0).getClass().getDeclaredFields()).
                    filter(field -> !isStatic(field.getModifiers())).collect(Collectors.toList());

            //do not include static fields
            fields.addAll(Arrays.stream(objects.get(0).getClass().getSuperclass().getDeclaredFields()).
                    filter(field -> !isStatic(field.getModifiers())).collect(Collectors.toList()));

            Object rowData[][] = new String[objects.size()][fields.size()];
            Object columnNames[] = new String[fields.size()];
            int restrictedColumnNr = 0;

            for (int i = 0; i < objects.size(); i++) {
                int j = 0;
                Object object = objects.get(i);
                for (Field field : fields) {
                    if (!isRestrictedField(field.getName())) {
                        field.setAccessible(true);
                        Object value;
                        try {
                            value = field.get(object);
                            rowData[i][j] = getValueColumn(field.getName(), value);
                            if (i == 0) {
                                columnNames[j] = field.getName();
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            if (LOGGER.isLoggable(Level.WARNING)) {
                                LOGGER.log(Level.WARNING, e.getMessage());
                            }
                        }
                        j++;
                    } else {
                        if (i == 0) {
                            restrictedColumnNr++;
                        }
                    }
                }
            }
            Object finalColumnNames[] = new String[fields.size() - restrictedColumnNr];
            System.arraycopy(columnNames, 0, finalColumnNames, 0, fields.size() - restrictedColumnNr);
            return new DefaultTableModel(rowData, finalColumnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return isCellEditableTable(row, column);
                }
            };
        }
        return new DefaultTableModel();
    }

    public List<User> getRegularUsers() {
        return new ArrayList<>(model.getRegularUsers());
    }

    public void addUser(String userName, String password) {
        if (isEmpty(userName) || isEmpty(password)) {
            view.showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = new User(userName, password, model.getRegularRole());
            user = userService.save(user);
            model.addUser(user);
        } catch (BllException e) {
            view.showMessage(e.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
    }

    public String getUsernameById(Long id) {
        return userService.findById(id).getUsername();
    }

    public void deleteUser(Long id) {
        userService.delete(id);
        model.deleteUser(id);
    }

    public void updateUser(Long id, String userName, String password, boolean changePassword) {
        if (isEmpty(userName)) {//password is validated on server
            view.showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newPass;

        if (changePassword) {
            newPass = password;
        } else {
            newPass = userService.findById(id).getPassword();
        }

        try {
            User user = new User(userName, newPass, roleService.findByName("user"));
            user = userService.update(id, user, changePassword);
            user.setId(id);
            model.addUser(user);
        } catch (BllException e) {
            view.showMessage(e.getMessage(), 2);
        }
    }

    public void deletePlant(Long id) {
        plantService.delete(id);
        model.deletePlant(id);
    }

    public String getPlantTypeById(Long id) {
        return plantService.findById(id).getType();
    }

    public int getPlantAverageLifeById(Long id) {
        return plantService.findById(id).getAverageLife();
    }

    public int getPlantWaterRequirements(Long id) {
        return plantService.findById(id).getWaterRequirements();
    }

    public int getPlantStockSize(Long id) {
        return plantService.findById(id).getStockSize();
    }


    public int getPlantPlotSize(Long id) {
        return plantService.findById(id).getPlotSize();
    }

    public void addPlant(String type, int averageLife, int waterRequirements, int plotDimensions
            , int stockSize) {
        if (isEmpty(type)) {
            view.showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Plant plant = plantService.save(new Plant(type, averageLife, waterRequirements, plotDimensions, stockSize));
            model.addPlant(plant);
        } catch (BllException e) {
            view.showMessage(e.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
    }

    public void updatePlant(Long id, String type, int averageLife, int waterRequirements,
                            int plotSize, int stockSize) {
        if (isEmpty(type)) {
            view.showMessage("Cannot be Empty!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Plant plant = new Plant(type, averageLife, waterRequirements, plotSize, stockSize);
            plantService.update(id, plant);
            plant.setId(id);
            model.addPlant(plant);
        } catch (BllException e) {
            view.showMessage(e.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
    }

    public List<Plant> getPlants() {
        return new ArrayList<>(model.getPlants());
    }

    public void generateReportTxt(String selectedPath) {
        reportGenerator.generateReport(ReportType.TXT).generateFileWithGarden(selectedPath, plantedPlantService.findAll(),
                plotService.findAll(), model.getGarden());
    }

    public void generateReportPdf(String selectedPath) {
        reportGenerator.generateReport(ReportType.PDF).generateFileWithGarden(selectedPath, plantedPlantService.findAll(),
                plotService.findAll(), model.getGarden());
    }

    //check if a field in a class is restricted and shouldn't be shown in a table
    protected boolean isRestrictedField(String name) {
        return false;
    }

    @Override
    public void update(ChannelObservable o, Channel channnel, Object arg) {
        String msg = (String) arg;

        String[] line = msg.split("#");
        if (msg.startsWith("login")) {
            view.showTooltip("<html>new log in:<br/>" + line[1] + "</html>");
        }
    }

    class UpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            update();
        }
    }

    class InsertListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            insert();
        }
    }

    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    }

    class WindowAdapterListener extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            closeWindow();
        }
    }

    public abstract void update();

    public abstract void insert();

    public abstract void delete();

    public abstract void refreshTable();

    protected void closeWindow() {
        authenticationManager.removeNotificationObserver(Channel.LOGIN, this);
    }
}
