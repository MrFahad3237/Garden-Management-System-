package Controller.service;

import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.User;

import java.util.List;
import java.util.logging.Level;

public class UserService extends GenericService<User> {

    public UserService(ClientConnection client) {
        super(client);
    }

    public User findByUserName(String userName) {
        try {
            String cmd = "findByUsername#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(userName);

            return (User) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public List<User> findRegularUsers() {
        try {
            String cmd = "findRegularUsers#" + tClass.getSimpleName();
            client.sendToServer(cmd);

            return (List<User>) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public User update(Long id, User object, boolean changePassword) {
        try {
            String cmd = "update#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);
            client.sendToServer(object);
            client.sendToServer(changePassword);

            return (User) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

}
