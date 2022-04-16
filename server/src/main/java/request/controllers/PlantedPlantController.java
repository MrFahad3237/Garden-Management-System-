package request.controllers;

import bll.service.GenericService;
import model.PlantedPlant;
import bll.observer.Channel;
import request.ClientHandler;

public class PlantedPlantController extends CrudController<PlantedPlant> {
    public PlantedPlantController(GenericService<PlantedPlant> genericService, ClientHandler clientHandler) {
        super(genericService, clientHandler);
    }

    @Override
    public void update() {
        clientHandler.notifyObserversWithMessage(Channel.DATA_CHANGE, "change#plantedplant");
        super.update();
    }

    @Override
    public void save() {
        PlantedPlant obj = (PlantedPlant) clientHandler.receiveFromClient();
        PlantedPlant resObj = service.save(obj);
        clientHandler.sendToClient(resObj);

        clientHandler.notifyObserversWithMessage(Channel.DATA_CHANGE, "change#plantedplant");
        clientHandler.notifyObserversWithMessage(Channel.PLANT_AT, "plantAt#" + resObj.getX() + "#" +
                resObj.getY() + "#" + clientHandler.getClientInfo().get("login"));//this notification is sent to the admin
    }
}
