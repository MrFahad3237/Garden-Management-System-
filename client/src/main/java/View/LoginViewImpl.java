package View;

import javax.swing.*;

public class LoginViewImpl implements LoginView {

    @Override
    public String[] showPasswordDialog() {
        Object[] a = new Object[4];
        a[0] = "Username";
        a[1] = new JTextField();
        a[2] = "Password";
        a[3] = new JTextField();
        if (JOptionPane.showConfirmDialog(null, a, "", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JTextField f1 = (JTextField) a[1];
            JTextField f2 = (JTextField) a[3];

            String userName = f1.getText();
            String password = f2.getText();
            return new String[]{userName, password};
        }
        return new String[]{};
    }

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "", JOptionPane.ERROR_MESSAGE);
    }
}
