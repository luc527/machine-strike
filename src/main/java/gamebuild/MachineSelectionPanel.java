package gamebuild;

import assets.MachineImageMap;
import assets.Machines;
import logic.Direction;
import logic.Machine;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class MachineSelectionPanel extends JComponent
{
    private static final int MACHINES_PER_ROW = 4;
    private static final int SIDE_PX = 48;

    private final int n;
    private final int rows;
    private final Image[][] machines;
    private final boolean[][] selected;

    private int cursorRow;
    private int cursorCol;

    public MachineSelectionPanel()
    {
        this(Machines.all());
    }

    public MachineSelectionPanel(List<Machine> machines)
    {
        this.n = machines.size();
        this.cursorRow = 0;
        this.cursorCol = 0;

        this.rows = (n - 1) / MACHINES_PER_ROW + 1;  // n/MACHINES_PER_ROW rounded up

        this.machines = new Image[rows][MACHINES_PER_ROW];
        this.selected = new boolean[rows][MACHINES_PER_ROW];
        for (var row = 0; row < rows; row++) {
            this.machines[row] = new Image[MACHINES_PER_ROW];
            this.selected[row] = new boolean[MACHINES_PER_ROW];
        }

        for (var i = 0; i < n; i++) {
            this.machines[i / MACHINES_PER_ROW][i % MACHINES_PER_ROW] = MachineImageMap.get(machines.get(i).name());
        }

        // testing
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> moveCursor(Direction.NORTH);
                    case KeyEvent.VK_DOWN -> moveCursor(Direction.SOUTH);
                    case KeyEvent.VK_LEFT -> moveCursor(Direction.WEST);
                    case KeyEvent.VK_RIGHT -> moveCursor(Direction.EAST);
                }
                repaint();
            }
        });
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(SIDE_PX  * MACHINES_PER_ROW, SIDE_PX * rows);
    }

    public void paintComponent(Graphics G)
    {
        var g = (Graphics2D) G;

        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            g.drawImage(machines[row][col], x, y, null);
        }

        var cy = cursorRow * SIDE_PX;
        var cx = cursorCol * SIDE_PX;
        g.setColor(new Color(32, 145, 100));
        g.setStroke(new BasicStroke(2.0f, BasicStroke.JOIN_MITER, BasicStroke.CAP_ROUND));
        g.drawRect(cx, cy, SIDE_PX, SIDE_PX);
    }

    private int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public void moveCursor(Direction dir)
    {
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            var lastCol = cursorRow == rows - 1 ? n % MACHINES_PER_ROW - 1 : MACHINES_PER_ROW - 1;
            cursorCol = clamp(cursorCol + offset, 0, lastCol);
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            var lastRow = cursorCol >= n % MACHINES_PER_ROW ? rows - 2 : rows - 1;
            cursorRow = clamp(cursorRow + offset, 0, lastRow);
        }
    }

}
