package View;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GardenViewImpl extends JFrame implements GardenView {

    private Random random = new Random();
    private int gridSize;
    private int numberOfGridsHorizontal;
    private int numberOfGridsVertical;
    private Map<String, Integer> usedColors;
    private JComponent[][] grid;
    private JPanel panel;
    private JButton addPlantBtn = new JButton("Add new Plant");
    private JButton addRequestBtn = new JButton("Add new Plant Request");


    public GardenViewImpl() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        panel = new JPanel();
        JPanel panel2 = new JPanel();

        content.add(panel, BorderLayout.NORTH);
        content.add(panel2, BorderLayout.WEST);

        panel2.add(addPlantBtn);
        panel2.add(addRequestBtn);

        usedColors = new HashMap<>();
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void refresh() {
        //reinitialize panel
        panel.removeAll();
        for (int i = 0; i < numberOfGridsVertical; i++) {
            for (int j = 0; j < numberOfGridsHorizontal; j++) {
                Component component = grid[i][j];

                if (component == null) {
                    panel.add(new JPanel());
                } else {
                    component.setSize(new Dimension(gridSize, gridSize));
                    panel.add(grid[i][j]);
                }
            }
        }
        setVisible(true);
    }

    @Override
    public void setGridSize(int size) {
        gridSize = size;
    }

    @Override
    public void setNumberOfGrids(int nrH, int nrV) {
        grid = new JComponent[nrV][nrH];
        numberOfGridsHorizontal = nrH;
        numberOfGridsVertical = nrV;
        panel.setLayout(new GridLayout(nrV, nrH));
    }

    @Override
    public void addButton(int x, int y, String name) {
        IndexButton jButton = new IndexButton("P");
        jButton.setPosX(x);
        jButton.setPosY(y);

        Integer colorValue = null;

        if (usedColors.containsKey(name)) {
            colorValue = usedColors.get(name);
        } else {
            do {
                colorValue = random.nextInt();
            } while (usedColors.values().contains(colorValue));
            usedColors.put(name, colorValue);
        }

        jButton.setBackground(new Color(colorValue));
        grid[y][x] = jButton;
    }

    @Override
    public void addButtonListener(ActionListener mal) {
        for (int i = 0; i < numberOfGridsVertical; i++) {
            for (int j = 0; j < numberOfGridsHorizontal; j++) {
                Component component = grid[i][j];
                if (component instanceof JButton) {
                    ((JButton) component).addActionListener(mal);
                }
            }
        }
    }

    @Override
    public void addPlantButtonListener(ActionListener mal) {
        addPlantBtn.addActionListener(mal);
    }

    @Override
    public void addRequestButtonListener(ActionListener mal) {
        addRequestBtn.addActionListener(mal);
    }

    @Override
    public void addWindowClosingListener(WindowAdapter mal) {
        addWindowListener(mal);
    }

    @Override
    public int getGridSize() {
        return gridSize;
    }

    @Override
    public int getNumberOfGridsHorizontal() {
        return numberOfGridsHorizontal;
    }

    @Override
    public int getNumberOfGridsVertical() {
        return numberOfGridsVertical;
    }

    @Override
    public void addLabel(int j, int i, String s, String s1) {
        JLabel label = new JLabel("id=" + s + " type=" + s1);
        label.setToolTipText("id=" + s + " type=" + s1);
        Integer colorValue = null;
        int red = 0;
        int green = 0;
        int blue = 0;

        if (usedColors.containsKey("plant" + s1)) {//colors for plants and plots are saved in the same hashmap
            colorValue = usedColors.get("plant" + s1);
        } else {
            do {
                //generate only light colors
                red = random.nextInt(150) + 100;
                green = random.nextInt(150) + 100;
                blue = random.nextInt(150) + 100;
                Color color = new Color(red, green, blue);
                colorValue = color.getRGB();
            } while (usedColors.values().contains(colorValue));
            usedColors.put("plant" + s1, colorValue);
        }

        label.setBackground(new Color(colorValue));
        label.setOpaque(true);
        grid[i][j] = label;
    }

    @Override
    public void showTooltip(String msg, int x, int y) {
        BalloonTip balloonTip = new BalloonTip(
                grid[x][y],
                new JLabel(msg),
                new RoundedBalloonStyle(5, 5, Color.WHITE, Color.BLACK),
                BalloonTip.Orientation.LEFT_BELOW,
                BalloonTip.AttachLocation.ALIGNED,
                15,
                25,
                true);
        TimingUtils.showTimedBalloon(balloonTip, 3000);
    }

    @Override
    public boolean createDialog(Object... a) {
        return JOptionPane.showConfirmDialog(null, a, "INSERT", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
}
