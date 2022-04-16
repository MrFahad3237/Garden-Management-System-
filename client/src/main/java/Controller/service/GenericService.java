package Controller.service;

import Controller.execptions.BllException;
import model.connection.ClientConnection;
import model.GenericModel;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericService<T extends GenericModel> {
    protected final static Logger LOGGER = Logger.getLogger(GenericService.class.getName());

    protected final Class<T> tClass;
    protected ClientConnection client;

    @SuppressWarnings("unchecked")
    public GenericService(ClientConnection client) {
        this.client = client;
        this.tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T findById(Long id) {
        try {
            String cmd = "findById#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);

            return (T) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public List<T> findAll() {
        try {
            String cmd = "findAll#" + tClass.getSimpleName();
            client.sendToServer(cmd);

            return (List<T>) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public T save(T object) {
        try {
            String cmd = "save#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(object);

            return (T) client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public void update(Long id, T object) {
        try {
            String cmd = "update#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);
            client.sendToServer(object);

            client.receiveFromServer();

        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            String cmd = "delete#" + tClass.getSimpleName();
            client.sendToServer(cmd);
            client.sendToServer(id);

            client.receiveFromServer();
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

}
