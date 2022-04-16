package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class StandingViewImpl extends JFrame implements StandingView {
    private JTable table;
    private Container content;
    private JPanel panel;
    protected JButton btnOption = new JButton("Choose option");

    public StandingViewImpl() {
        this.table = new JTable();
        //do not exit program when closing JFrame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        content = getContentPane();
        content.setLayout(new BorderLayout());
        panel = new JPanel();
        JPanel panel2 = new JPanel(new GridLayout(10, 1));
        panel2.add(btnOption);
        content.add(panel2, BorderLayout.WEST);
        this.setVisible(true);
    }

    public void addFrame() {
        panel.removeAll();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
        content.add(panel, BorderLayout.CENTER);
        this.setSize(400, 500);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setName("Standing");
    }

    @Override
    public void setTable(DefaultTableModel tTable) {
        this.table = new JTable(tTable);
        this.table.setPreferredScrollableViewportSize(new Dimension(800, 500));
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public void addOptionButtonListener(ActionListener mal) {
        btnOption.addActionListener(mal);
    }

    @Override
    public boolean createDialog(Object... a) {
        return JOptionPane.showConfirmDialog(null, a, "Dialog", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
}
