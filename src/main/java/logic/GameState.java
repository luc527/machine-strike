package logic;

import constants.Constants;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameState
{
    private Player currentPlayer;
    private final Board board;
    private final Piece[][] pieces;

    public GameState(Player startingPlayer, Board board, Piece[][] startingPieces)
    {
        this.currentPlayer = startingPlayer;
        this.board = board;
        var height = Constants.BOARD_ROWS;
        var width = Constants.BOARD_COLS;
        this.pieces = new Piece[height][width];
        for (var i = 0; i < height; i++)
            System.arraycopy(startingPieces[i], 0, this.pieces[i], 0, width);
    }

    public Player currentPlayer() { return currentPlayer; }
    public Board board() { return board; }

    public Piece pieceAt(int r, int c) {
        return pieces[r][c];
    }

    public static Set<Coord> generateAvailablePositions(int sourceRow, int sourceCol, int movementRange)
    {
        if (movementRange < 1) return Set.of(Coord.create(sourceRow, sourceCol));
        var set = new HashSet<Coord>();

        BiConsumer<Integer, Integer> makeRow = (blanks, row) -> {
            var col = sourceCol - movementRange;
            for (var j=0; j<blanks; j++) col++;
            for (var j=0; j<2*movementRange+1-2*blanks; j++) set.add(Coord.create(row, col++));
        };

        var row = sourceRow - movementRange;
        for (var blanks = movementRange; blanks >= 0; blanks--) {
            makeRow.accept(blanks, row++);
        }
        for (var blanks = 1; blanks <= movementRange; blanks++) {
            makeRow.accept(blanks, row++);
        }

        return set;
    }

    public void movePiece(int srcRow, int srcCol, int destRow, int destCol)
    {
        if (srcRow == destRow && srcCol == destCol) {
            return;
        }
        var moved = pieces[srcRow][srcCol];
        if (moved == null) {
            throw new RuntimeException("Attempted to move piece from empty space (row:"+srcRow+", col:"+srcCol+")");
        }
        if (pieces[destRow][destCol] != null) {
            throw new RuntimeException("Attempted to move piece to a non-empty space (row:"+destRow+", col:"+destCol+")");
        }
        pieces[destRow][destCol] = moved;
        pieces[srcRow][srcCol] = null;
    }
}
