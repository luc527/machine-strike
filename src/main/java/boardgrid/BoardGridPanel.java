package boardgrid;

import assets.MachineImageMap;
import assets.Machines;
import assets.TerrainColorMap;
import graphics.Palette;
import logic.Direction;
import logic.Machine;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.EnumMap;

public class BoardGridPanel extends JPanel
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int SIDE_PX = Constants.BOARD_SIDE_PX;
    private static final EnumMap<Direction, Double> DIR_TO_THETA;

    static {
        DIR_TO_THETA = new EnumMap<>(Direction.class);
        DIR_TO_THETA.put(Direction.NORTH,   0.0);
        DIR_TO_THETA.put(Direction.WEST,    Math.PI/2);
        DIR_TO_THETA.put(Direction.SOUTH,   Math.PI);
        DIR_TO_THETA.put(Direction.EAST,  3*Math.PI/2);
    }

    private final BoardGridModel grid;

    private Color cursorColor;
    private boolean showCursor;
    private Runnable onConfirm;
    private Runnable onCancel;

    public BoardGridPanel(BoardGridModel grid)
    {
        this.grid = grid;

        showCursor = false;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                if (c == KeyEvent.VK_ENTER) {
                    if (onConfirm != null) onConfirm.run();
                } else if (c == KeyEvent.VK_ESCAPE) {
                    if (onCancel != null) onCancel.run();
                } else if (c == 'q') {
                    grid.rotateCarriedMachine(false);
                } else if (c == 'e') {
                    grid.rotateCarriedMachine(true);
                } else {
                    switch (k) {
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
        return new Dimension(COLS * SIDE_PX, ROWS * SIDE_PX);
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        super.paintComponent(G);

        var g = (Graphics2D) G;
        grid.iterate((row, col, terrain, piece, available) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            var terrainColor = TerrainColorMap.get(terrain);
            g.setColor(terrainColor);
            g.fillRect(x, y, SIDE_PX, SIDE_PX);
            g.setColor(terrainColor.darker());
            g.drawRect(x, y, SIDE_PX, SIDE_PX);

            piece.ifPresent(p -> {
                drawMachine(p.machine(), p.direction(), g, x, y);
                g.setColor(Palette.transparentColor(p.player()));
                g.fillRect(x, y, Constants.BOARD_SIDE_PX, Constants.BOARD_SIDE_PX);
            });

            if (available) {
                g.setColor(Palette.transparentYellow);
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }
        });

        if (showCursor) {
            var cursor = grid.getCursor();
            var cy = cursor.row() * SIDE_PX;
            var cx = cursor.col() * SIDE_PX;
            g.setColor(cursorColor);
            g.setStroke(new BasicStroke(5.0f));
            g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
            drawMachine(
                    Machines.get(grid.getCarriedMachine()),
                    grid.getCarriedMachineDirection(),
                    g, cx, cy
            );
        }
    }

    private void drawMachine(Machine machine, Direction direction, Graphics2D g, int x, int y)
    {
        var img = MachineImageMap.get(machine.name());

        var xformSaved = g.getTransform();
        g.rotate(DIR_TO_THETA.get(direction), x + SIDE_PX / 2.0, y + SIDE_PX / 2.0);
        g.drawImage(img, x, y, null);

        // TODO also show armored/weak points

        g.setTransform(xformSaved);
    }

    public void setCursorColor(Color c)
    {
        this.cursorColor = c;
    }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        if (!b) grid.endInteraction();
        showCursor = b;
        repaint();
    }

    public void onCancel(Runnable r)
    {
        this.onCancel = r;
    }

    public void onConfirm(Runnable r)
    {
        this.onConfirm = r;
    }
}
