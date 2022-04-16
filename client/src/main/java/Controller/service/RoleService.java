package Controller.service;

import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.UserRole;

import java.util.logging.Level;

public class RoleService extends GenericService<UserRole> {

    public RoleService(ClientConnection client) {
        super(client);
    }

    public UserRole findByName(String name) {
        try {
            String cmd = "findByName#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(name);
            return (UserRole) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

}
