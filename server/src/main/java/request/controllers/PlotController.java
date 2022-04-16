package request.controllers;

import bll.service.GenericService;
import model.Plot;
import request.ClientHandler;

public class PlotController extends CrudController<Plot> {
    public PlotController(GenericService<Plot> service, ClientHandler clientHandler) {
        super(service, clientHandler);
    }
}
