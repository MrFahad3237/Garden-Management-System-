package request.controllers;

import bll.service.GenericService;
import bll.service.UserService;
import model.User;
import request.ClientHandler;
import request.security.RequestAccess;

@RequestAccess(role = "admin")
public class UserController extends CrudController<User> {

    public UserController(GenericService<User> service, ClientHandler clientHandler) {
        super(service, clientHandler);
    }

    @Override
    public void update() {
        Long id = (Long) clientHandler.receiveFromClient();
        User obj = (User) clientHandler.receiveFromClient();

        boolean changePassword = (boolean) clientHandler.receiveFromClient();
        if (changePassword) {
            obj.setPassword(clientHandler.getAuthenticationManager().encode(obj.getPassword()));
        }

        service.update(id, obj);
        clientHandler.sendToClient(obj);
    }

    @Override
    public void save() {
        User obj = (User) clientHandler.receiveFromClient();
        obj.setPassword(clientHandler.getAuthenticationManager().encode(obj.getPassword()));

        User resObj = service.save(obj);
        clientHandler.sendToClient(resObj);
    }

    public void findByUsername() {
        String userName = (String) clientHandler.receiveFromClient();
        clientHandler.sendToClient(((UserService) service).findByUserName(userName));
    }

    public void findRegularUsers() {
        clientHandler.sendToClient(((UserService) service).findRegularUsers());
    }
}
