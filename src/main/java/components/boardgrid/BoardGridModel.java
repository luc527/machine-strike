package components.boardgrid;

import logic.Utils;
import logic.*;
import constants.Constants;

import java.util.Set;

/**
 * Logically organizes the board into a grid wherein a cursor can move.
 * Encapsulates the board (terrains) and the cursor, and the positions available for the cursor.
 */
public abstract class BoardGridModel
{
    protected static final int ROWS = Constants.BOARD_ROWS;
    protected static final int COLS = Constants.BOARD_COLS;

    protected final Board board;
    protected Coord cursor;

    protected Runnable onMove = () -> {};

    public BoardGridModel(Board board)
    {
        this.board = board;
        cursor = Coord.create(0, 0);
    }

    public void setCursor(Coord cursor)
    { this.cursor = cursor; }

    public Coord getCursor()
    { return cursor; }

    public void onMove(Runnable r) { this.onMove = r; }


    public abstract boolean isAvailable(int row, int col);

    public boolean isAvailable(Coord coord)
    { return isAvailable(coord.row(), coord.col()); }

    /**
     * Attempts to move the cursor in the given direction;
     * won't move if the resulting position is unavailable.
     * @param dir The direction to which move the cursor.
     */
    public void moveCursor(Direction dir)
    {
        var result = Coord.create(cursor);
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            result = result.withCol(Utils.clamp(cursor.col() + offset, 0, COLS - 1));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            result = result.withRow(Utils.clamp(cursor.row() + offset, 0, ROWS - 1));
        }
        if (isAvailable(result)) {
            cursor = result;
            onMove.run();
        }
    }

    public Board getBoard() { return board; }

}
