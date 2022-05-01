package gamebuild.machinegrid;

import assets.MachineImageMap;
import assets.Machines;
import graphics.Palette;
import logic.Coord;
import logic.Direction;
import logic.Machine;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MachineGridPanel extends JPanel
{
    protected static final int SIDE_PX = Constants.BOARD_SIDE_PX;

    protected final MachineGridModel grid;
    protected final Color cursorColor;
    protected boolean showCursor;
    private Runnable onPressEnter;
    private Runnable onPressBackspace;

    public MachineGridPanel(MachineGridModel grid, Color cursorColor)
    {
        this.grid = grid;
        this.cursorColor = cursorColor;
        this.showCursor = false;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (onPressEnter != null) {
                        onPressEnter.run();
                    }
                } else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    if (onPressBackspace != null) {
                        onPressBackspace.run();
                    }
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_RIGHT -> grid.moveCursor(Direction.EAST);
                        case KeyEvent.VK_UP    -> grid.moveCursor(Direction.NORTH);
                        case KeyEvent.VK_LEFT  -> grid.moveCursor(Direction.WEST);
                        case KeyEvent.VK_DOWN  -> grid.moveCursor(Direction.SOUTH);
                    }
                }
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(
                grid.cols() * SIDE_PX,
                grid.rows() * SIDE_PX
        );
    }

    public void paintMachines(Graphics2D g)
    {
        grid.iterate((row, col) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            var machineName = grid.machineAt(Coord.create(row, col));
            var machine = Machines.get(machineName);
            for (var dir : Direction.iter()) {
                var pt = machine.point(dir);
                if (pt == Machine.Point.EMPTY) continue;
                var tmp = g.getTransform();
                g.rotate(dir.theta(), x + SIDE_PX/2, y + SIDE_PX/2);
                var color = pt == Machine.Point.ARMORED ? Palette.armoredPt : Palette.weakPt;
                g.setColor(color);
                g.fillRect(x+11, y+1, SIDE_PX-22, 10);
                g.setTransform(tmp);
            }
            g.drawImage(MachineImageMap.get(machineName), x, y, null);
        });
    }

    public void paintCursor(Graphics2D g)
    {
        var cy = grid.cursor().row() * SIDE_PX;
        var cx = grid.cursor().col() * SIDE_PX;
        g.setColor(cursorColor);
        g.setStroke(new BasicStroke(5.0f));
        g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        super.paintComponent(G);
        var g = (Graphics2D) G;
        if (showCursor) {
            paintCursor(g);
        }
        paintMachines(g);
    }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        showCursor = b;
        repaint();
    }

    public void onPressEnter(Runnable r)
    {
        this.onPressEnter = r;
    }

    public void onPressBackspace(Runnable r)
    {
        this.onPressBackspace = r;
    }
}
