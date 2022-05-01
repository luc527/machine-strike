package components.boardgrid;

import assets.MachineImageMap;
import assets.TerrainColorMap;
import components.Palette;
import logic.Direction;
import logic.Machine;
import constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BoardGridPanel extends JPanel
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int SIDE_PX = Constants.BOARD_SIDE_PX;

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
        return new Dimension(COLS * SIDE_PX + 1, ROWS * SIDE_PX + 1);
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
                    grid.getCarriedMachine(),
                    grid.getCarriedMachineDirection(),
                    g, cx, cy
            );
        }
    }

    private void drawMachine(Machine machine, Direction direction, Graphics2D g, int x, int y)
    {
        var img = MachineImageMap.get(machine);

        var xformSaved = g.getTransform();
        g.rotate(direction.theta(), x + SIDE_PX / 2.0, y + SIDE_PX / 2.0);

        for (var dir : Direction.iter()) {
            var pt = machine.point(dir);
            if (pt == Machine.Point.EMPTY) continue;
            var xformSaved2 = g.getTransform();
            g.rotate(dir.theta(), x + SIDE_PX / 2.0, y + SIDE_PX / 2.0);
            var color = pt == Machine.Point.ARMORED ? Palette.armoredPt : Palette.weakPt;
            g.setColor(color);
            g.fillRect(x+11, y+1, SIDE_PX-22, 10);
            g.setTransform(xformSaved2);
        }

        g.drawImage(img, x, y, null);

        g.setTransform(xformSaved);
    }

    public void setCursorColor(Color c)
    {
        this.cursorColor = c;
    }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
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
