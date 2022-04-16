package request.controllers;

import bll.service.GenericService;
import bll.service.RoleService;
import model.UserRole;
import request.ClientHandler;

public class UserRoleController extends CrudController<UserRole> {
    public UserRoleController(GenericService<UserRole> service, ClientHandler clientHandler) {
        super(service, clientHandler);
    }

    public void findByName() {
        String name = (String) clientHandler.receiveFromClient();

        UserRole userRole = ((RoleService) service).findByName(name);

        clientHandler.sendToClient(userRole);
    }

}
