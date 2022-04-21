package boardgrid;

import logic.*;
import utils.Constants;

import java.util.Set;
import java.util.Optional;
import java.util.function.Function;

public class BoardGridModel implements IBoardGridModel
{
    public interface BoardGridIterator
    {
        void exec(int row, int col, Terrain terrain, Optional<Piece> piece, boolean available);
    }

    public interface PieceProvider extends Function<ICoord, Optional<Piece>> {}

    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;

    private final Board board;
    private final CoordState cursor;
    private final PieceProvider pieceProvider;

    // Machine carried by the cursor as the player moves it across the board
    private String carriedMachine;

    // Positions the player is currently allowed to move within
    private Set<ICoord> availablePositions;

    public BoardGridModel(Board board, PieceProvider pieceProvider)
    {
        this.board = board;
        this.pieceProvider = pieceProvider;
        cursor = new CoordState(0, 0);
        availablePositions = Set.of();
    }

    @Override
    public void startInteraction(ICoord cursor, Set<ICoord> availablePositions, String carriedMachine)
    {
        this.carriedMachine = carriedMachine;
        this.availablePositions = availablePositions;
        this.cursor.set(cursor);
    }

    @Override
    public void endInteraction()
    {
        this.availablePositions = Set.of();
    }

    @Override
    public ICoord getCursor()
    {
        return cursor;
    }

    public String getCarriedMachine()
    {
        return this.carriedMachine;
    }

    @Override
    public void iterate(BoardGridIterator it)
    {
        // TODO make some kind of coord cache, so I can get the Coord object for any row and col without necessarely
        //  having to actually create a new one each time (like Java does with Integer objects up to like 128)
        //  and also call it a factory ;)

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var coord = CoordCache.get(row, col);
                var piece = pieceProvider.apply(coord);
                var available = availablePositions.contains(coord);
                it.exec(row, col, board.get(row, col), piece, available);
            }
        }
    }

    private int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public void moveCursor(Direction dir)
    {
        var result = new CoordState(cursor.row(), cursor.col());
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            result.setCol(clamp(cursor.col() + offset, 0, COLS - 1));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            result.setRow(clamp(cursor.row() + offset, 0, ROWS - 1));
        }
        if (availablePositions.contains(result)) {
            cursor.set(result);
        }
    }

}
