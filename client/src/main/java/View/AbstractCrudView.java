package View;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public abstract class AbstractCrudView extends JFrame implements CrudView {

    private static final long serialVersionUID = 1L;
    protected Container content;
    protected JPanel panel;
    protected JPanel panel2;
    protected JPanel panel3;
    protected DefaultTableModel tableModel;
    protected JTable jTable = new JTable();
    protected JButton btnUpdate = new JButton("Update");
    protected JButton btnInsert = new JButton("Insert");
    protected JButton btnDelete = new JButton("Delete");

    public AbstractCrudView() {
        //do not exit program when closing JFrame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        content = getContentPane();
        content.setLayout(new BorderLayout());
        panel = new JPanel();

        panel2 = new JPanel();
        panel3 = new JPanel(new GridLayout(20, 1));

        content.add(panel2, BorderLayout.NORTH);

        panel3.add(btnUpdate);
        panel3.add(btnInsert);
        panel3.add(btnInsert);
        panel3.add(btnDelete);

        content.add(panel3, BorderLayout.WEST);
    }

    /**
     * Adds the JTable to 'panel' then 'panel' to the current frame
     */
    public void addFrame() {
        panel.removeAll();
        JScrollPane scrollPane = new JScrollPane(jTable);
        panel.add(scrollPane);
        content.add(panel, BorderLayout.CENTER);
        this.setSize(400, 500);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setName("Gardening SYSTEM");
        this.setVisible(true);
    }

    public void showMessage(String msg, int warningMessage) {
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

    public void showTooltip(String msg) {
        BalloonTip balloonTip = new BalloonTip(
                btnDelete,
                new JLabel(msg),
                new RoundedBalloonStyle(5, 5, Color.WHITE, Color.BLACK),
                BalloonTip.Orientation.LEFT_BELOW,
                BalloonTip.AttachLocation.ALIGNED,
                15,
                25,
                true);
        TimingUtils.showTimedBalloon(balloonTip, 3000);
    }

    public void setTable(DefaultTableModel tTable) {
        this.jTable = new JTable(tTable);
        this.jTable.setPreferredScrollableViewportSize(new Dimension(800, 500));
        tableModel = tTable;
    }

    public JTable getTable() {
        return jTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public boolean createDialog(Object... a) {
        return JOptionPane.showConfirmDialog(null, a, "Dialog", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public void addUpdateButtonListener(ActionListener mal) {
        btnUpdate.addActionListener(mal);
    }

    public void addInsertButtonListener(ActionListener mal) {
        btnInsert.addActionListener(mal);
    }

    public void addDeleteButtonListener(ActionListener mal) {
        btnDelete.addActionListener(mal);
    }

    @Override
    public void addWindowClosingListener(WindowAdapter mal) {
        addWindowListener(mal);
    }
}
