package request.controllers;

import bll.service.GenericService;
import model.Plant;
import request.ClientHandler;

public class PlantController extends CrudController<Plant> {
    public PlantController(GenericService<Plant> service, ClientHandler clientHandler) {
        super(service, clientHandler);
    }
}
