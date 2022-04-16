package model.connection;

import Controller.execptions.BllException;
import Controller.execptions.ServerConnectionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionImpl implements ClientConnection{
    private final static Logger LOGGER = Logger.getLogger(ClientConnection.class.getName());

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final int TIMEOUT = 3000;

    public ClientConnectionImpl(int port, String ip) {

        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port), TIMEOUT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerConnectionException("Cannot connect to Server!", e);
        }
    }

    @Override
    synchronized public void sendToServer(Object msg) {
        try {
            if (clientSocket == null || out == null) {
                throw new ServerConnectionException("no socket");
            }

            out.writeObject(msg);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerConnectionException("Connection lost!", e);
        }

    }

    @Override
    public synchronized Object receiveFromServer() {
        try {
            Object res = in.readObject();

            if (res instanceof Exception) {
                throw (Exception) res;
            }

            return res;
        } catch (BllException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return null;
    }

    private void close() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }

            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        sendToServer("close");
        LOGGER.log(Level.INFO, "closing connection with server...");
        close();
    }
}
