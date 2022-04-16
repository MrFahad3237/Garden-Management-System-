package request.controllers;

import bll.service.GenericService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.GenericModel;
import request.ClientHandler;
import request.security.RequestAccess;

import java.util.logging.Logger;

@RequiredArgsConstructor
@RequestAccess
public abstract class CrudController<T extends GenericModel> implements Controller {
    private final static Logger LOGGER = Logger.getLogger(CrudController.class.getName());

    @NonNull
    protected final GenericService<T> service;

    @NonNull
    protected final ClientHandler clientHandler;

    public void findById() {
        Long id = (Long) clientHandler.receiveFromClient();
        clientHandler.sendToClient(service.findById(id));
    }

    public void findAll() {
        clientHandler.sendToClient(service.findAll());
    }

    public void delete() {
        Long id = (Long) clientHandler.receiveFromClient();
        service.delete(id);
        clientHandler.sendToClient(true);
    }

    @SuppressWarnings("unchecked")
    public void update() {
        Long id = (Long) clientHandler.receiveFromClient();
        T obj = (T) clientHandler.receiveFromClient();

        service.update(id, obj);
        clientHandler.sendToClient(obj);
    }

    @SuppressWarnings("unchecked")
    public void save() {
        T obj = (T) clientHandler.receiveFromClient();
        T resObj = service.save(obj);
        clientHandler.sendToClient(resObj);
    }
}
