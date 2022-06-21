package gamebuild.machineSelection;

import logic.Utils;
import components.machinegrid.MachineGridModel;
import components.machinegrid.MachineGridPanel;
import components.Palette;
import logic.Coord;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Extends the MachineGridPanel to not only display the machines in the grid,
 * but also display amounts associated with each, with a current amount and
 * a maximum amount, given by the IPlayerMachineSelectionModel. It also
 * shows the victory points associated with each machine, since there is
 * not only a limit on amount, for each piece, but also a global limit on
 * the sum of the selected machines' victory points. This is also provided
 * by the IPlayerMachineSelectionModel.
 */
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

            if (selected == available || !machSelModel.selectable(machine)) {
                g.setColor(Palette.transparentBlack);
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }

            var stringHeight = 10;
            //
            // Show amount
            //
            var amountString = String.format("%d / %d", selected, available);
            var amountBackColor  = selected == 0 ? Color.DARK_GRAY : Palette.darkYellow;
            var amountFrontColor = selected == 0 ? Color.LIGHT_GRAY : Palette.yellow;
            Utils.drawOutlinedString(g, x+2, y+2+stringHeight, amountString, amountBackColor, amountFrontColor);

            //
            // Show victory points
            //
            Utils.drawOutlinedString(g, x+2, y+2+SIDE_PX-stringHeight, ""+machine.victoryPoints(), Palette.darkGreen, Palette.green);
        });
    }
}
