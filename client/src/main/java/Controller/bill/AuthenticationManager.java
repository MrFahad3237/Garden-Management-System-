package Controller.bill;

import Controller.execptions.BllException;
import Controller.observer.Channel;
import Controller.observer.ChannelObserver;
import model.connection.Client;
import model.connection.ClientConnection;
import model.connection.NotificationConnection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.User;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class AuthenticationManager {
    final static Logger LOGGER = Logger.getLogger(AuthenticationManager.class.getName());

    @NonNull
    private final ClientConnection clientConnection;
    @NonNull
    private final NotificationConnection notificationConnection;
    @Getter
    private Client client;//contains basic information which can be used in specific cases
    @Getter
    private User user;//contains all the user information

    public boolean validateUser(String userName, String password) {
        return login(userName, password);
    }

    public void addNotificationObserver(Channel channel, ChannelObserver observer) {
        notificationConnection.addObserverToChannel(channel, observer);
    }

    public void removeNotificationObserver(Channel channel, ChannelObserver observer) {
        notificationConnection.removeObserverFromChannel(channel, observer);
    }

    public void logout() {
        clientConnection.closeConnection();//cannot be null
        notificationConnection.close();//cannot be null
    }

    private boolean login(String userName, String password) {

        clientConnection.sendToServer("login#" + userName + "#" + password);

        try {
            String role = (String) clientConnection.receiveFromServer();
            user = (User) clientConnection.receiveFromServer();

            if (role != null) {
                client = new Client(userName, password, role);

                new Thread(notificationConnection).start();
            }
        } catch (BllException e) {//when logged in, throws LoggedInException
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return false;
        }
        return true;
    }
}
