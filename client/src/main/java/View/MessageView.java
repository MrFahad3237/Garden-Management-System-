package View;

import javax.swing.*;

public interface MessageView {
    default void showMessage(String msg, int warningMessage) {
        switch (warningMessage) {
            case JOptionPane.WARNING_MESSAGE:
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.WARNING_MESSAGE);
                break;
            case JOptionPane.ERROR_MESSAGE:
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.ERROR_MESSAGE);
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
}
