package gamebuild;

import assets.MachineImageMap;
import assets.Machines;
import logic.Machine;
import utils.Constants;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class MachineSelectionPanel extends JComponent
{
    private int n;
    private List<Machine> machines;
    private boolean[] selected;


    public MachineSelectionPanel()
    {
        this(Machines.all());
    }

    public MachineSelectionPanel(List<Machine> machines)
    {
        this.n = machines.size();
        this.machines = machines;
        this.selected = new boolean[n];
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(Constants.TILE_WIDTH_PX * machines.size(), Constants.TILE_HEIGHT_PX);
    }

    public void paintComponent(Graphics g)
    {
        var x = 0;
        var y = 0;
        for (int i = 0; i < n; i++) {
            var name = machines.get(i).name();
            System.out.println(name);
            var img = MachineImageMap.get( machines.get(i).name() );
            g.drawImage(img, x, y, null);
            x += Constants.TILE_WIDTH_PX;
        }
    }
}
