package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public interface CrudView extends MessageView {

    /**
     * Adds the JTable to 'panel' then 'panel' to the current frame
     */
    void addFrame();

    void setTable(DefaultTableModel tTable);

    JTable getTable();

    DefaultTableModel getTableModel();

    void setTableModel(DefaultTableModel tableModel);

    void addUpdateButtonListener(ActionListener mal);

    void addInsertButtonListener(ActionListener mal);

    void addDeleteButtonListener(ActionListener mal);

    void addWindowClosingListener(WindowAdapter mal);

    void showTooltip(String msg);

    boolean createDialog(Object... a);
}
