package gamebuild;

import assets.MachineImageMap;
import assets.Machines;
import logic.Coord;
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
    private final Image[][] machineImages;
    private final boolean[][] selected;

    private final String[][] machineNames;
    private final Map<String, Coord> positionByName;  // inverted index of machineNames

    private final Color cursorColor;
    private final Coord cursor;
    private boolean showCursor;

    public MachineSelectionPanel(Color cursorColor)
    {
        this(Machines.all(), cursorColor);
    }

    public MachineSelectionPanel(List<Machine> machines, Color cursorColor)
    {
        this.n = machines.size();
        this.cursorColor = cursorColor;
        this.showCursor = false;
        this.cursor = new Coord(0, 0);

        this.rows = (n - 1) / MACHINES_PER_ROW + 1;  // n/MACHINES_PER_ROW rounded up

        this.machineImages = new Image[rows][MACHINES_PER_ROW];
        this.machineNames = new String[rows][MACHINES_PER_ROW];
        this.selected = new boolean[rows][MACHINES_PER_ROW];
        for (var row = 0; row < rows; row++) {
            this.machineImages[row] = new Image[MACHINES_PER_ROW];
            this.machineNames[row] = new String[MACHINES_PER_ROW];
            this.selected[row] = new boolean[MACHINES_PER_ROW];
        }

        positionByName = new HashMap<>();

        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var name = machines.get(i).name();
            this.machineImages[row][col] = MachineImageMap.get(name);
            this.machineNames[row][col] = name;
            positionByName.put(name, new Coord(row, col));
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
            g.setColor(cursorColor/**/);
            g.setStroke(new BasicStroke(5.0f, BasicStroke.JOIN_MITER, BasicStroke.CAP_ROUND));
            g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
        }

        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            g.drawImage(machineImages[row][col], x, y, null);
        }

        g.setStroke(new BasicStroke());
        g.setColor(new Color(0, 0, 0, .2f));  // For selected machines

        for (var row = 0; row < rows; row++) {
            for (var col = 0; col < MACHINES_PER_ROW; col++) {
                var y = row * SIDE_PX;
                var x = col * SIDE_PX;
                if (selected[row][col]) {
                    g.fillRect(x, y, SIDE_PX, SIDE_PX);
                }
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
        return machineNames[cursor.row()][cursor.col()];
    }

    public void showCursor(boolean show)
    {
        this.showCursor = show;
    }

    public void setCursorOver(String machineName)
    {
        cursor.set(positionByName.get(machineName));
    }

    public void setMachineUnderCursorSelected(boolean sel)
    {
        selected[cursor.row()][cursor.col()] = sel;
    }
}
