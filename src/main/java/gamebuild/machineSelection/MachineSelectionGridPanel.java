package gamebuild.machineSelection;

import components.machinegrid.MachineGridModel;
import components.machinegrid.MachineGridPanel;
import components.Palette;
import logic.Coord;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MachineSelectionGridPanel extends MachineGridPanel
{
    private final IPlayerMachineSelectionModel machSelModel;

    public MachineSelectionGridPanel(IPlayerMachineSelectionModel machSelModel, MachineGridModel grid, Color cursorColor)
    {
        super(grid, cursorColor);
        this.machSelModel = machSelModel;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics G)
    {
        super.paintComponent(G);

        var g = (Graphics2D) G;

        grid.iterate((row, col) -> {
            var x = SIDE_PX * col;
            var y = SIDE_PX * row;

            var machine = grid.machineAt(Coord.create(row, col));
            var selected = machSelModel.selectedAmount(machine);
            var available = machSelModel.availableAmount(machine);

            var selectable =  machine.victoryPoints()
                    +  machSelModel.currentVictoryPoints()
                    <= machSelModel.maxVictoryPoints();
            if (selected == available || !selectable) {
                g.setColor(Palette.transparentBlack);
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }

            //
            // Show amount
            //
            var string = String.format("%d / %d", selected, available);

            var amountBackColor  = selected == 0 ? Color.DARK_GRAY : Palette.darkYellow;
            var amountFrontColor = selected == 0 ? Color.LIGHT_GRAY : Palette.yellow;

            var stringHeight = 10;
            var sx = x + 2;
            var sy = y + 2 + stringHeight;

            g.setColor(amountBackColor);
            for (var yoff = -1; yoff <= 1; yoff++)
                for (var xoff = -1; xoff <= 1; xoff++)
                    g.drawString(string, sx + xoff, sy + yoff);
            g.setColor(amountFrontColor);
            g.drawString(string, sx, sy);

            //
            // Show victory points
            //
            string = String.format("%d", machine.victoryPoints());
            sx = x + 2;
            sy = y + 2 + SIDE_PX - stringHeight;
            g.setColor(Palette.darkGreen);
            for (var yoff = -1; yoff <= 1; yoff++)
                for (var xoff = -1; xoff <= 1; xoff++)
                    g.drawString(string, sx + xoff, sy + yoff);
            g.setColor(Palette.green);
            g.drawString(string, sx, sy);
        });
    }
}
