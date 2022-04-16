package Controller.execptions;

import javax.swing.*;

public final class GlobalExceptionHandlerUtil {
    private GlobalExceptionHandlerUtil() {
    }

    public static void enableGlobalException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                String msg;
                if (e.getMessage().isEmpty()) {
                    msg = "No connection!";
                }
                else{
                    msg = e.getMessage();
                }
                JOptionPane.showMessageDialog(null, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
