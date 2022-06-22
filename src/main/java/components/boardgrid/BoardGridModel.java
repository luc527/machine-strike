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
    public interface TerrainIterator
    { void exec(int row, int col, Terrain terrain); }

    public interface AvailableIterator
    { void exec(int row, int col); }

    protected static final int ROWS = Constants.BOARD_ROWS;
    protected static final int COLS = Constants.BOARD_COLS;

    protected final Board board;
    protected Coord cursor;
    protected Set<Coord> availablePositions;

    protected Runnable onMove = () -> {};

    public BoardGridModel(Board board)
    {
        this.board = board;
        cursor = Coord.create(0, 0);
        availablePositions = Set.of();
    }

    public void setCursor(Coord cursor)
    { this.cursor = cursor; }

    public Coord getCursor()
    { return cursor; }

    public void setAvailablePositions(Set<Coord> availablePositions)
    { this.availablePositions = availablePositions; }

    public void onMove(Runnable r) { this.onMove = r; }

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
        if (availablePositions.isEmpty() || availablePositions.contains(result)) {
            cursor = result;
            onMove.run();
        }
    }

    public Board getBoard() { return board; }

    public void iterateTerrain(TerrainIterator it)
    {
        for (var row = 0; row < ROWS; row++)
            for (var col = 0; col < COLS; col++)
                it.exec(row, col, board.get(row, col));
    }

    public void iterateAvailable(AvailableIterator it)
    {
        for (var pos : availablePositions) {
            var row = pos.row();
            var col = pos.col();
            if (row >= 0 && row < ROWS && col >= 0 && col <= COLS)
                it.exec(row, col);
        }

    }

}
