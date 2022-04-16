package View;

import Controller.bill.AuthenticationManager;
import Controller.execptions.BllException;
import Controller.execptions.OutOfStockException;
import Controller.execptions.RedundantDataException;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;
import Controller.observer.ChannelObserver;
import Controller.service.PlantService;
import Controller.service.PlantedPlantService;
import Controller.service.PlotService;
import Controller.service.UserPlantRequestService;
import Controller.validators.IntegerValidatorUtil;
import model.Garden;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import model.UserPlantRequest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;

public class GardenController implements ChannelObserver {
    private GardenView view;
    private GardenModel model;
    private PlantService plantService;
    private PlantedPlantService plantedPlantService;
    private PlotService plotService;
    private UserPlantRequestService requestService;
    private AuthenticationManager authenticationManager;

    public GardenController(GardenView view, GardenModel model,
                            PlantService plantService,
                            PlantedPlantService plantedPlantService, PlotService plotService,
                            UserPlantRequestService requestService,
                            AuthenticationManager authenticationManager) {
        this.view = view;
        this.model = model;
        this.plantService = plantService;
        this.plantedPlantService = plantedPlantService;
        this.plotService = plotService;
        this.requestService = requestService;
        this.authenticationManager = authenticationManager;

        authenticationManager.addNotificationObserver(Channel.DATA_CHANGE, this);
        authenticationManager.addNotificationObserver(Channel.PLANT_AT, this);

        //this needs to be called first
        loadGarden();
        view.addPlantButtonListener(new AddPlantButtonListener());
        view.addWindowClosingListener(new WindowAdapterListener());
        view.addRequestButtonListener(new RequestButtonListener());
        updateGrids();
    }

    public void updateGrids() {
        view.setGridSize(50);
        view.setNumberOfGrids(model.getGarden().getWidth(), model.getGarden().getHeight());

        for (int i = 0; i < model.getGarden().getHeight(); i++) {
            for (int j = 0; j < model.getGarden().getWidth(); j++) {
                if (model.getGardenRepr()[i][j][0] != null) {
                    if (model.getGardenRepr()[i][j][0].equals("plot")) {
                        view.addButton(j, i, model.getGardenRepr()[i][j][1]);
                    } else if (model.getGardenRepr()[i][j][0].equals("plant")) {
                        view.addLabel(j, i, model.getGardenRepr()[i][j][1], model.getGardenRepr()[i][j][2]);
                    }
                }
            }
        }
        view.addButtonListener(new AddButtonListener());
        view.refresh();
    }

    public void showPlantDialog() {

        List<Plant> plants = model.getPlants();
        Object types[] = new Object[plants.size()];
        int i = 0;
        for (Plant plant : plants) {
            types[i] = plant;
            i++;
        }

        Object[] a = new Object[2];
        a[0] = "Choose a plant";
        a[1] = new JComboBox(types);

        if (view.createDialog(a)) {
            JComboBox f1 = (JComboBox) a[1];
            Plant plant = (Plant) f1.getSelectedItem();
            model.setChosenPlantId(plant.getId());
        }
    }

    public void loadGarden() {
        List<Plot> plots = plotService.findAll();
        model.setPlants(plantService.findAll());
        List<PlantedPlant> plantedPlants = plantedPlantService.findAll();

        model.setGarden(new Garden(new HashSet<>(plots), new HashSet<>(plantedPlants)));

        model.setGardenRepr(new String[model.getGarden().getHeight()][model.getGarden().getWidth()][3]);

        //build the representation of the garden
        for (Plot plot : plots) {
            for (int i = plot.getUpperY(); i < plot.getUpperY() + plot.getHeight(); i++) {
                for (int j = plot.getUpperX(); j < plot.getUpperX() + plot.getWidth(); j++) {
                    model.getGardenRepr()[i][j][0] = "plot";
                    model.getGardenRepr()[i][j][1] = plot.getId().toString();
                }
            }
        }

        for (PlantedPlant plantedPlant : plantedPlants) {
            for (int i = plantedPlant.getY(); i < plantedPlant.getY() + plantedPlant.getPlant().getPlotSize(); i++) {
                for (int j = plantedPlant.getX(); j < plantedPlant.getX() + plantedPlant.getPlant().getPlotSize(); j++) {
                    model.getGardenRepr()[i][j][0] = "plant";
                    model.getGardenRepr()[i][j][1] = plantedPlant.getId().toString();
                    model.getGardenRepr()[i][j][2] = plantedPlant.getPlant().getType();
                }
            }
        }
    }

    public boolean plantAt(int x, int y) {
        if (model.getChosenPlantId() != null) {//plant has been chosen
            Plant plant = plantService.findById(model.getChosenPlantId());
            //check if there is available space
            for (int i = y; i < y + plant.getPlotSize(); i++) {
                for (int j = x; j < x + plant.getPlotSize(); j++) {
                    if (i >= model.getGarden().getHeight()) {
                        return false;
                    }
                    if (j >= model.getGarden().getWidth()) {
                        return false;
                    }
                    if (model.getGardenRepr()[i][j][0] == null) {
                        return false;//no plot here
                    }
                    if (!model.getGardenRepr()[i][j][0].equals("plot")) {
                        return false;//not enough space
                    }
                }
            }

            //enough space so let's plant it
            //first save the new planted plant in the garden
            PlantedPlant plantedPlant = new PlantedPlant(x, y, plant);

            try {
                plantedPlant = plantedPlantService.save(plantedPlant);

                model.addPlantedPlant(plantedPlant);

                for (int i = y; i < y + plant.getPlotSize(); i++) {
                    for (int j = x; j < x + plant.getPlotSize(); j++) {
                        model.getGardenRepr()[i][j][0] = "plant";
                        model.getGardenRepr()[i][j][1] = plantedPlant.getId().toString();
                        model.getGardenRepr()[i][j][2] = plant.getType();
                    }
                }
                return true;
            } catch (RedundantDataException e) {
                view.showMessage(e.getMessage(), JOptionPane.WARNING_MESSAGE);
                //need to update data
                loadGarden();
                updateGrids();
            }
        }
        return false;
    }

    @Override
    public void update(ChannelObservable o, Channel channel, Object arg) {
        String msg = (String) arg;

        String[] line = msg.split("#");
        if (channel == Channel.DATA_CHANGE) {
            //need to update data
            loadGarden();
            updateGrids();
        } else if (channel == Channel.PLANT_AT) {
            int x = Integer.valueOf(line[1]);
            int y = Integer.valueOf(line[2]);
            String user = line[3];
            view.showTooltip(user, y, x);
        }
    }

    private void removeFromObservable() {
        authenticationManager.removeNotificationObserver(Channel.DATA_CHANGE, this);
        authenticationManager.removeNotificationObserver(Channel.PLANT_AT, this);
    }

    class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            IndexButton button = (IndexButton) e.getSource();
            int x = button.getPosX();
            int y = button.getPosY();

            //plant new plant
            try {
                if (!plantAt(x, y)) {
                    view.showMessage("Cannot plant at that location" +
                            " or you didn't choose a plant!", JOptionPane.ERROR_MESSAGE);
                }
                updateGrids();
            } catch (OutOfStockException exp) {
                view.showMessage(exp.getMessage(), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    class AddPlantButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showPlantDialog();
        }
    }

    class WindowAdapterListener extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            removeFromObservable();

            if (authenticationManager.getClient().getRole() != null &&
                    authenticationManager.getClient().getRole().equals("user")) {//exit only if regular user
                authenticationManager.logout();
                System.exit(0);
            }
        }
    }

    class RequestButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            Plant[] plants = new Plant[model.getPlants().size()];
            int i = 0;
            for (Plant plant : model.getPlants()) {
                plants[i] = plant;
                i++;
            }

            Object[] a = new Object[6];
            a[0] = "type";
            a[1] = new JComboBox<>(plants);

            a[2] = "Amount";
            a[3] = new JTextField();

            if (view.createDialog(a[0], a[1], a[2], a[3])) {
                Plant plant = (Plant) ((JComboBox) a[1]).getSelectedItem();

                if (plant.getStockSize() > 0) {
                    view.showMessage("Stock is larger than 0",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    IntegerValidatorUtil.validateString(((JTextField) a[3]).getText());

                    int amount = Integer.parseInt(((JTextField) a[3]).getText());

                    UserPlantRequest request = new UserPlantRequest(plant,
                            authenticationManager.getUser(), (long) amount);
                    requestService.save(request);

                } catch (BllException exc) {
                    view.showMessage(exc.getMessage(),
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}
