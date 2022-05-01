package components;

import assets.TerrainColorMap;
import logic.Board;
import constants.Constants;

import javax.swing.*;
import java.awt.*;

public class BoardIcon implements Icon
{
    private static int SIDE_PX = 16;

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
                var y = by + row * SIDE_PX;
                var x = bx + col * SIDE_PX;
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
                g.setColor(color.darker());
                g.drawRect(x, y, SIDE_PX, SIDE_PX);
            }
        }
    }

    // Plus one for the outlines (drawRect)

    public int getIconWidth()
    {
        return Constants.BOARD_COLS * SIDE_PX + 1;
    }

    public int getIconHeight()
    {
        return Constants.BOARD_ROWS * SIDE_PX + 1;
    }
}
