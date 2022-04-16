package request;

import bll.observer.Channel;
import bll.observer.ChannelObservable;
import bll.observer.ChannelObserver;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationHandler implements ChannelObserver, Runnable {
    private final static Logger LOGGER = Logger.getLogger(NotificationHandler.class.getName());

    private ServerStarter serverStarter;//object which created this
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    @Getter
    private Set<Channel> channelsListeningTo;

    @Getter
    private Map<String, String> clientInfo;
    private boolean stop = false;
    private final BlockingQueue<Map.Entry<Channel, String>> notifications;
    private static int NOTIFICATION_STORAGE_SIZE = 100;

    public NotificationHandler(Socket clientSocket, ServerStarter serverStarter, Map<String, String> clientInfo) {
        this.clientSocket = clientSocket;
        this.serverStarter = serverStarter;
        this.clientInfo = clientInfo;
        notifications = new ArrayBlockingQueue<>(NOTIFICATION_STORAGE_SIZE);
        channelsListeningTo = new HashSet<>();

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    final public void close() throws IOException {
        stop = true;
        serverStarter.removeConnection(this);

        if (clientSocket != null) {
            clientSocket.close();
        }

        if (out != null) {
            out.close();
        }

        if (in != null) {
            in.close();
        }
    }

    public void addListeningChannel(Channel channel) {

        //cannot add duplicates because of set
        channelsListeningTo.add(channel);
    }

    public void removeListeningChannel(Channel channel) {
        channelsListeningTo.remove(channel);
    }

    @Override
    public void run() {
        while (!stop) {
            //process messages
            try {
                Map.Entry<Channel, String> notification = notifications.take();
                String msg = notification.getValue();
                Channel channel = notification.getKey();

                if (msg.startsWith("closeNotification") &&
                        msg.split("#")[1].equals(clientInfo.get("login"))) {
                    close();
                } else {
                    out.writeObject(msg);
                    out.writeObject(channel);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
    }

    @Override
    public void update(ChannelObservable o, Channel channnel, Object arg) {
        try {
            notifications.offer(new AbstractMap.SimpleEntry<>(channnel, (String) arg), 3,
                    TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.log(Level.WARNING, "timeout in notification update");
            }
        }
    }

}

