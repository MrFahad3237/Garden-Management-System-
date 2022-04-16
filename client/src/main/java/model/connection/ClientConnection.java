package model.connection;

public interface ClientConnection {

    void sendToServer(Object msg);

    Object receiveFromServer();

    void closeConnection();
}