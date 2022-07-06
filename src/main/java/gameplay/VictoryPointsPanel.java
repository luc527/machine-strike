package gameplay;

import javax.swing.*;

public class VictoryPointsPanel extends JPanel
{
    private JLabel p1lb;
    private JLabel p2lb;

    public VictoryPointsPanel()
    {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JLabel("Victory Points:               "));
        add(p1lb = new JLabel("P1: 0"));
        add(new JLabel("               "));
        add(p2lb = new JLabel("P2: 0"));
    }

    public void setVP(int p1vp, int p2vp)
    {
        p1lb.setText("P1: " + p1vp);
        p2lb.setText("P2: " + p2vp);
    }
}
