package Controller.service;

import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.UserPlantRequest;

import java.util.List;
import java.util.logging.Level;

public class UserPlantRequestService extends GenericService<UserPlantRequest> {
    public UserPlantRequestService(ClientConnection client) {
        super(client);
    }

    public void acceptRequest(Long id) {
        try {
            String cmd = "acceptRequest#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);

            client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void denyRequest(Long id) {
        try {
            String cmd = "denyRequest#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);

            client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public List<UserPlantRequest> findAllAccepted() {
        try {
            String cmd = "findAllAccepted#" + tClass.getSimpleName();
            client.sendToServer(cmd);

            return (List<UserPlantRequest>) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public List<UserPlantRequest> findAllDenied() {
        try {
            String cmd = "findAllDenied#" + tClass.getSimpleName();
            client.sendToServer(cmd);

            return (List<UserPlantRequest>) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public List<UserPlantRequest> findNotAccepted() {
        try {
            String cmd = "findNotAccepted#" + tClass.getSimpleName();
            client.sendToServer(cmd);

            return (List<UserPlantRequest>) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }
}
