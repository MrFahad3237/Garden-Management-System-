package View;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class IndexButton extends JButton {
    private int posX;
    private int posY;

    public IndexButton(String s) {
        super(s);
    }
}
