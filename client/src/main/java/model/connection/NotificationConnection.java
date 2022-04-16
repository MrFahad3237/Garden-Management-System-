package model.connection;

import Controller.execptions.ServerConnectionException;
import Controller.observer.Channel;
import Controller.observer.ChannelObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationConnection extends ChannelObservable implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(NotificationConnection.class.getName());

    private int port;
    private String ip;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean stop = false;

    public NotificationConnection(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public void close() {
        stop = true;

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
    public void run() {
        try {
            startConnection();
            Object msg;
            Object channel;

            while (!stop) {
                msg = in.readObject();
                channel = in.readObject();
                handleMessageFromClient(channel, msg);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            close();
        }
    }

    private void startConnection() {
        try {
            clientSocket = new Socket(ip, port);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerConnectionException("Cannot connect to Server!", e);
        }
    }

    private synchronized void handleMessageFromClient(Object ch, Object obj) {
        String msg = (String) obj;
        Channel channel = (Channel) ch;
        notifyObserversWithMessage(channel, msg);
    }
}
