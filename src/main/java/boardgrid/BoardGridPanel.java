package boardgrid;

import assets.MachineImageMap;
import assets.TerrainColorMap;
import graphics.Palette;
import logic.Direction;
import logic.Piece;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BoardGridPanel extends JPanel
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int SIDE_PX = Constants.BOARD_SIDE_PX;

    private final IBoardGridModel grid;

    private Color cursorColor;
    private boolean showCursor;
    private Runnable onConfirm;
    private Runnable onCancel;

    public BoardGridPanel(IBoardGridModel grid)
    {
        this.grid = grid;
        this.cursorColor = cursorColor;

        showCursor = false;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                if (c == KeyEvent.VK_ENTER) {
                    if (onConfirm != null) onConfirm.run();
                } else if (c == KeyEvent.VK_ESCAPE) {
                    if (onCancel != null) onCancel.run();
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
        return new Dimension(
                COLS * SIDE_PX,
                ROWS * SIDE_PX
        );
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        super.paintComponent(G);

        var g = (Graphics2D) G;
        grid.iterate((row, col, terrain, piece, available) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            var color = TerrainColorMap.get(terrain);
            g.setColor(color);
            g.fillRect(x, y, SIDE_PX, SIDE_PX);
            g.setColor(color.darker());
            g.drawRect(x, y, SIDE_PX, SIDE_PX);

            if (piece.isPresent()) {
                drawPiece(piece.get(), g, x, y);
            }

            if (available) {
                g.setColor(new Color(0.8f, 1f, 1f, 0.3f));
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
            g.drawImage(MachineImageMap.get(grid.getCarriedMachine()),
                        cx, cy, null);
        }
    }

    private void drawPiece(Piece piece, Graphics g, int x, int y)
    {
        var img = MachineImageMap.get(piece.machine().name());
        g.drawImage(img, x, y, null);
        var c = Palette.color(piece.player());
        var color = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255*0.3));
        g.setColor(color);
        g.fillRect(x, y, img.getWidth(null), img.getHeight(null));
        // TODO also consider direction and so on
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
