package gamebuild;

import assets.MachineImageMap;
import assets.TerrainColorMap;
import logic.Board;
import logic.Coord;
import logic.Direction;
import utils.Constants;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int SIDE_PX = Constants.BOARD_SIDE_PX;

    private final Coord cursor;
    private Color cursorColor;
    private boolean showCursor;
    private String carriedMachine;  // carried by the cursor

    private final Board board;

    public BoardPanel(Board board)
    {
        this.cursor = new Coord(0, 0);
        this.showCursor = false;
        this.board = board;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(COLS * SIDE_PX + 1, ROWS * SIDE_PX + 1);
    }

    @Override
    public void paintComponent(Graphics G)
    {
        // TODO for now just colors, but later investigate how to convey depth (z, along with x and y)
        //  probably just with larger/smaller side lengths

        var g = (Graphics2D) G;

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var y = row * SIDE_PX;
                var x = col * SIDE_PX;
                var terrain = board.get(row, col);
                var color = TerrainColorMap.get(terrain);
                g.setColor(color);
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
                g.setColor(color.darker());
                g.drawRect(x, y, SIDE_PX, SIDE_PX);
            }
        }

        if (showCursor) {
            var cy = cursor.row() * SIDE_PX;
            var cx = cursor.col() * SIDE_PX;
            g.setColor(cursorColor);
            g.setStroke(new BasicStroke(5.0f));
            g.drawRect(cx+2, cy+2, SIDE_PX-5, SIDE_PX-5);
            g.drawImage(MachineImageMap.get(carriedMachine), cx, cy, null);
        }
    }

    public void setCursorColor(Color c)
    {
        this.cursorColor = c;
    }

    public void setFocused(boolean b)
    {
        if (b) requestFocusInWindow();
        showCursor = b;
    }

    public void carryMachine(String machineName)
    {
        this.carriedMachine = machineName;
    }

    private int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public Coord moveCursor(Direction dir)
    {
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            cursor.setCol(clamp(cursor.col() + offset, 0, COLS - 1));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            cursor.setRow(clamp(cursor.row() + offset, 0, ROWS - 1));
        }
        return cursor;
    }

    public void setCursorOver(Coord coord)
    {
        cursor.set(coord);
    }
}
