package gameplay;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel
{
    public static final long EMPTY_FLAGS      = 0;
    public static final long ENTER_TO_SELECT  = 1 << 0;
    public static final long ENTER_TO_PLACE   = 1 << 1;
    public static final long K_TO_ATTACK      = 1 << 2;
    public static final long F_TO_FINISH_TURN = 1 << 3;
    public static final long ESC_TO_DESELECT  = 1 << 4;

    private final JLabel[] labels = {
        new JLabel("ENTER to select the piece"),
        new JLabel("ENTER to place the piece"),
        new JLabel("K to attack"),
        new JLabel("F to finish turn"),
        new JLabel("ESC to de-select the piece"),
    };

    public InfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        for (var label : labels) add(label);
    }

    public void display(long bits) {
        for (int i=0; i<labels.length; i++) {
            labels[i].setVisible((bits & (1L << i)) != 0);
        }
        repaint();
    }

}
