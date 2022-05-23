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
    protected static final int ROWS = Constants.BOARD_ROWS;
    protected static final int COLS = Constants.BOARD_COLS;
    protected static final int SIDE_PX = Constants.BOARD_SIDE_PX;

    protected final BoardGridModel grid;
    protected boolean showCursor;
    private Color cursorColor;

    // :PatternUsed dependency injection (not really a pattern but whatever)
    public BoardGridPanel(BoardGridModel grid)
    {
        this.grid = grid;
        showCursor = false;
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                var k = e.getKeyCode();
                switch (k) {
                    case KeyEvent.VK_RIGHT -> { grid.moveCursor(Direction.EAST);  repaint(); }
                    case KeyEvent.VK_UP    -> { grid.moveCursor(Direction.NORTH); repaint(); }
                    case KeyEvent.VK_LEFT  -> { grid.moveCursor(Direction.WEST);  repaint(); }
                    case KeyEvent.VK_DOWN  -> { grid.moveCursor(Direction.SOUTH); repaint(); }
                }
            }
        });
    }

    public void setCursorColor(Color c)
    { this.cursorColor = c; }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        showCursor = b;
        repaint();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(COLS * SIDE_PX + 1, ROWS * SIDE_PX + 1);
    }

    protected void paintTerrain(Graphics2D g)
    {
        var savedColor = g.getColor();
        grid.iterateTerrain((row, col, terrain) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            var terrainColor = TerrainColorMap.get(terrain);
            g.setColor(terrainColor);
            g.fillRect(x, y, SIDE_PX, SIDE_PX);
            g.setColor(terrainColor.darker());
            g.drawRect(x, y, SIDE_PX, SIDE_PX);
        });
        g.setColor(savedColor);
    }

    protected void paintAvailablePositions(Graphics2D g)
    {
        var savedColor = g.getColor();
        grid.iterateAvailable((row, col) -> {
            var y = row * SIDE_PX;
            var x = col * SIDE_PX;
            g.setColor(Palette.transparentYellow);
            g.fillRect(x, y, SIDE_PX, SIDE_PX);
        });
        g.setColor(savedColor);
    }

    protected void paintCursor(Graphics2D g)
    {
        if (!showCursor) return;

        var savedColor = g.getColor();
        var savedStroke = g.getStroke();

        var cursor = grid.getCursor();
        var cy = cursor.row() * SIDE_PX;
        var cx = cursor.col() * SIDE_PX;
        g.setColor(cursorColor);
        g.setStroke(new BasicStroke(5.0f));
        g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);

        g.setColor(savedColor);
        g.setStroke(savedStroke);
    }

    protected void panelPaintComponent(Graphics g)
    { super.paintComponent(g); }

    @Override
    protected void paintComponent(Graphics G)
    {
        panelPaintComponent(G);
        var g = (Graphics2D) G;
        paintTerrain(g);
        paintCursor(g);
        paintAvailablePositions(g);
    }

    // Available for all subclasses
    // TODO move this anyway, doesn't belong in here
    protected void drawMachine(Machine machine, Direction direction, Graphics2D g, int x, int y)
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

}
