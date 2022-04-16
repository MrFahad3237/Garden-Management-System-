package View;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public interface GardenView extends MessageView {

    void refresh();

    void setGridSize(int size);

    void setNumberOfGrids(int nrH, int nrV);

    void addButton(int x, int y, String name);

    void addButtonListener(ActionListener mal);

    void addPlantButtonListener(ActionListener mal);

    void addWindowClosingListener(WindowAdapter mal);

    void addRequestButtonListener(ActionListener mal);

    int getGridSize();

    int getNumberOfGridsHorizontal();

    int getNumberOfGridsVertical();

    void addLabel(int j, int i, String s, String s1);

    void showTooltip(String msg, int x, int y);

    boolean createDialog(Object... a);
}
