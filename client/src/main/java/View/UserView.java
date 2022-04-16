package View;

import java.awt.event.ActionListener;

public interface UserView extends CrudView {

    void addPlantButtonListener(ActionListener mal);

    void addGardenButtonListener(ActionListener mal);

    void addRequestButtonListener(ActionListener mal);

    void addStandingButtonListener(ActionListener mal);
}
