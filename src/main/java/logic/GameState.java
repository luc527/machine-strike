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
