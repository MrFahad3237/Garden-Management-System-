package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;

public interface StandingView {
    void addFrame();

    void setTable(DefaultTableModel tTable);

    JTable getTable();

    void addOptionButtonListener(ActionListener mal);

    boolean createDialog(Object... a);
}
