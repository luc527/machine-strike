package gamebuild.machinegrid;

import assets.MachineImageMap;
import logic.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MachineGridPanel extends JPanel
{
    private static final int SIDE_PX = 48;

    private final MachineGridModel grid;
    private final Color cursorColor;
    private boolean showCursor;

    public MachineGridPanel(MachineGridModel grid, Color cursorColor, Runnable selectionCallback)
    {
        this.grid = grid;
        this.cursorColor = cursorColor;
        this.showCursor = false;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                System.out.println(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    selectionCallback.run();
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

    @Override
    protected void paintComponent(Graphics G)
    {
        super.paintComponent(G);
        var g = (Graphics2D) G;

        if (showCursor) {
            var cy = grid.cursor().row() * SIDE_PX;
            var cx = grid.cursor().col() * SIDE_PX;
            g.setColor(cursorColor);
            g.setStroke(new BasicStroke(5.0f));
            g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
        }

        grid.iterate((row, col, machineName, amount) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            g.drawImage(MachineImageMap.get(machineName), x, y, null);

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

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        showCursor = b;
    }
}
