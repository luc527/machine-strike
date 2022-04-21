package gamebuild_old;

import assets.MachineImageMap;
import assets.Machines;
import gamebuild.MachineInventoryState;
import logic.CoordState;
import logic.Direction;
import logic.Machine;

import javax.swing.*;
import java.awt.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineSelectionPanel extends JComponent
{
    private static final int MACHINES_PER_ROW = 4;
    private static final int SIDE_PX = 48;

    private final int n;
    private final int rows;

    private final String[][] machines;
    private final Map<String, CoordState> positionByName;  // inverted index of machineNames
    private final MachineInventoryState inventory;

    private final Color cursorColor;
    private final CoordState cursor;
    private boolean showCursor;

    public MachineSelectionPanel(MachineInventoryState inventory, Color cursorColor)
    {
        this(Machines.all(), inventory, cursorColor);
    }

    public MachineSelectionPanel(List<Machine> machines, MachineInventoryState inventory, Color cursorColor)
    {
        n = machines.size();
        this.inventory = inventory;
        this.cursorColor = cursorColor;

        showCursor = false;
        cursor = new CoordState(0, 0);

        rows = (n - 1) / MACHINES_PER_ROW + 1;  // n/MACHINES_PER_ROW rounded up

        this.machines = new String[rows][MACHINES_PER_ROW];
        for (var row = 0; row < rows; row++) {
            this.machines[row] = new String[MACHINES_PER_ROW];
        }

        positionByName = new HashMap<>();

        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var name = machines.get(i).name();
            this.machines[row][col] = name;
            positionByName.put(name, new CoordState(row, col));
        }

        setFocusable(true);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(SIDE_PX  * MACHINES_PER_ROW, SIDE_PX * rows);
    }

    public void paintComponent(Graphics G)
    {
        var g = (Graphics2D) G;

        if (showCursor) {
            var cy = cursor.row() * SIDE_PX;
            var cx = cursor.col() * SIDE_PX;
            g.setColor(cursorColor);
            g.setStroke(new BasicStroke(5.0f, BasicStroke.JOIN_MITER, BasicStroke.CAP_ROUND));
            g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
        }

        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            g.drawImage(MachineImageMap.get(machines[row][col]), x, y, null);

            g.setStroke(new BasicStroke());
            var amount = inventory.getAmount(machines[row][col]);
            var amountString = String.valueOf(amount);
            // hacky way of doing outlines, but other stack overflow answers were pretty complicated
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
        }

    }

    private int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public String moveCursor(Direction dir)
    {
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            var lastCol = cursor.row() == rows - 1 ? n % MACHINES_PER_ROW - 1 : MACHINES_PER_ROW - 1;
            cursor.setCol(clamp(cursor.col() + offset, 0, lastCol));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            var lastRow = cursor.col() >= n % MACHINES_PER_ROW ? rows - 2 : rows - 1;
            cursor.setRow(clamp(cursor.row() + offset, 0, lastRow));
        }
        return machines[cursor.row()][cursor.col()];
    }

    public void setCursorOver(String machineName)
    {
        cursor.set(positionByName.get(machineName));
    }

    public String machineUnderCursor()
    {
        return machines[cursor.row()][cursor.col()];
    }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        showCursor = b;
    }
}