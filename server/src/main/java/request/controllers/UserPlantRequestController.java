package request.controllers;

import bll.observer.Channel;
import bll.service.GenericService;
import bll.service.UserPlantRequestService;
import model.UserPlantRequest;
import request.ClientHandler;
import request.security.RequestAccess;

public class UserPlantRequestController extends CrudController<UserPlantRequest> {

    public UserPlantRequestController(GenericService<UserPlantRequest> service, ClientHandler clientHandler) {
        super(service, clientHandler);
    }

    @RequestAccess(role = "admin")
    @Override
    public void findById() {
        super.findById();
    }

    @RequestAccess(role = "admin")
    @Override
    public void findAll() {
        super.findAll();
    }

    @RequestAccess(role = "admin")
    public void findAllAccepted() {
        clientHandler.sendToClient(((UserPlantRequestService) service).findAllAccepted());
    }

    @RequestAccess(role = "admin")
    public void findAllDenied() {
        clientHandler.sendToClient(((UserPlantRequestService) service).findAllDenied());
    }

    @RequestAccess(role = "admin")
    public void findNotAccepted() {
        clientHandler.sendToClient(((UserPlantRequestService) service).findNotAccepted());
    }

    @RequestAccess(role = "admin")
    @Override
    public void delete() {
        super.delete();
    }

    @RequestAccess(role = "admin")
    @Override
    public void update() {
        super.update();
    }

    @RequestAccess(role = "admin")
    public void acceptRequest() {
        Long id = (Long) clientHandler.receiveFromClient();
        ((UserPlantRequestService) service).acceptRequest(id);

        clientHandler.sendToClient(true);

        clientHandler.notifyObserversWithMessage(Channel.STANDING, "acceptRequest");
        clientHandler.notifyObserversWithMessage(Channel.DATA_CHANGE, "acceptRequest");
    }

    @RequestAccess(role = "admin")
    public void denyRequest() {
        Long id = (Long) clientHandler.receiveFromClient();
        ((UserPlantRequestService) service).denyRequest(id);

        clientHandler.sendToClient(true);

        clientHandler.notifyObserversWithMessage(Channel.STANDING, "denyRequest");
    }

    @Override
    public void save() {
        super.save();
        clientHandler.notifyObserversWithMessage(Channel.STANDING, "save");
    }
}
