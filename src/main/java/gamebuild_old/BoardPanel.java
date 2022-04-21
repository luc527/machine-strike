package gamebuild_old;

import assets.MachineImageMap;
import assets.TerrainColorMap;
import logic.Board;
import logic.CoordState;
import logic.Direction;
import logic.Piece;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class BoardPanel extends JPanel
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int SIDE_PX = Constants.BOARD_SIDE_PX;

    private final CoordState cursor;
    private boolean showCursor;
    private Color cursorColor;
    private String carriedMachine;  // carried by the cursor

    private final Board board;
    private final Function<CoordState, Piece> pieceProvider;
    private List<CoordState> availablePositions;

    public BoardPanel(Board board, Function<CoordState, Piece> pieceSupplier)
    {
        this.cursor = new CoordState(0, 0);
        this.showCursor = false;
        this.board = board;
        this.pieceProvider = pieceSupplier;
        this.availablePositions = List.of();
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

                var piece = pieceProvider.apply(new CoordState(row, col));
                if (piece != null) {
                    drawPiece(piece, g, x, y);
                }
            }
        }

        g.setColor(new Color(0.8f, 1f, 1f, 0.3f));
        for (var coord : availablePositions) {
            var y = coord.row() * SIDE_PX;
            var x = coord.col() * SIDE_PX;
            g.fillRect(x, y, SIDE_PX, SIDE_PX);
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

    private void drawPiece(Piece piece, Graphics g, int x, int y)
    {
        var img = MachineImageMap.get(piece.machine().name());
        g.drawImage(img, x, y, null);
        var c = piece.player().color();
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

    public CoordState moveCursor(Direction dir)
    {
        System.out.println("Moving cursor " + dir);
        var result = new CoordState(cursor.row(), cursor.col());
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            result.setCol(clamp(cursor.col() + offset, 0, COLS - 1));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            result.setRow(clamp(cursor.row() + offset, 0, ROWS - 1));
        }
        if (availablePositions.contains(result)) {
            System.out.println("OK!");
            cursor.set(result);
        } else System.out.println("Oh no!");
        return cursor;
    }

    public void setCursor(CoordState coord)
    {
        cursor.set(coord);
    }

    public void setAvailablePositions(List<CoordState> availablePositions)
    {
        this.availablePositions = availablePositions;
    }
}
