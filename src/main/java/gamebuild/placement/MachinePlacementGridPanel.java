package gamebuild.placement;

import gamebuild.IMachineInventory;
import gamebuild.machinegrid.MachineGridModel;
import gamebuild.machinegrid.MachineGridPanel;
import logic.CoordCache;

import java.awt.*;

public class MachinePlacementGridPanel extends MachineGridPanel
{
    private final IMachineInventory inventory;

    public MachinePlacementGridPanel(MachineGridModel grid, IMachineInventory inventory, Color cursorColor)
    {
        super(grid, cursorColor);
        this.inventory = inventory;
    }

    @Override
    public void paintComponent(Graphics G)
    {
        super.paintComponent(G);
        var g = (Graphics2D) G;

        grid.iterate((row, col) -> {
            var machine = grid.machineAt(CoordCache.get(row, col));
            var amount = inventory.getAmount(machine);

            var x = SIDE_PX * col;
            var y = SIDE_PX * row;
            var amountString = String.valueOf(amount);
            // hacky way of doing outlines; whatever
            var stringHeight = 12;
            var ax = x+2;
            var ay = y+2+stringHeight;
            g.setColor(Color.BLACK);
            for (var yoff = -1; yoff <= 1; yoff++)
                for (var xoff = -1; xoff <= 1; xoff++)
                    g.drawString(amountString, ax+xoff, ay+yoff);
            g.setColor(Color.WHITE);
            g.drawString(amountString, ax, ay);

            if (amount == 0) {
                g.setColor(new Color(0, 0, 0, .2f));
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }
        });
    }
}
