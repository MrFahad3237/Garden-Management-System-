package Controller.service;

import model.connection.ClientConnection;
import model.PlantedPlant;

public class PlantedPlantService extends GenericService<PlantedPlant> {
    public PlantedPlantService(ClientConnection client) {
        super(client);
    }
}
