package gameplay;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel
{
    public static final int ENTER_TO_SELECT = 1 << 0;
    public static final int ENTER_TO_PLACE  = 1 << 1;
    public static final int K_TO_ATTACK     = 1 << 2;

    private final JLabel[] labels = {
            new JLabel("ENTER to select the piece"),
            new JLabel("ENTER to place the piece"),
            new JLabel("K to attack"),
    };

    public InfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        for (var i=0; i<labels.length; i++) {
            add(labels[i]);
        }
    }

    public void display(int bits) {
        for (var i=0; i<labels.length; i++) {
            labels[i].setVisible((bits & (1 << i)) != 0);
        }
        repaint();
    }

}
