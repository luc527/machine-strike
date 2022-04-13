package graphics;

import assets.TerrainColorMap;
import logic.Board;
import utils.Constants;

import javax.swing.*;
import java.awt.*;

public class BoardIcon implements Icon
{
    private static int TILE_WIDTH = 16;
    private static int TILE_HEIGHT = 16;

    private Board board;

    public BoardIcon(Board board)
    {
        this.board = board;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int bx, int by)
    {
        for (int row = 0; row < Constants.BOARD_ROWS; row++) {
            for (int col = 0; col < Constants.BOARD_COLS; col++) {
                var color = TerrainColorMap.get(board.get(row, col));
                g.setColor(color);
                var y = by + row * TILE_HEIGHT;
                var x = bx + col * TILE_WIDTH;
                g.fillRect(x, y, TILE_WIDTH, TILE_HEIGHT);
                g.setColor(color.darker());
                g.drawRect(x, y, TILE_WIDTH, TILE_HEIGHT);
            }
        }
    }

    // Plus one for the outlines (drawRect)

    public int getIconWidth()
    { return Constants.BOARD_COLS * TILE_WIDTH + 1; }

    @Override
    public int getIconHeight()
    { return Constants.BOARD_ROWS * TILE_HEIGHT + 1; }
}
