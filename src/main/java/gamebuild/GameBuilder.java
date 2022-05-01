package gamebuild;

import logic.*;
import constants.Constants;

// :PatternUsed Builder
// Builds an initial game state

public class GameBuilder
{
    private Player startingPlayer;
    private Board board;
    private Piece[][] piecesByPosition;

    public GameBuilder()
    {
        this.startingPlayer = null;
        this.board = null;
        this.piecesByPosition = new Piece[Constants.BOARD_ROWS][Constants.BOARD_COLS];
    }

    public void setStartingPlayer(Player p)
    {
        this.startingPlayer = p;
    }

    public void setBoard(Board b)
    {
        this.board = b;
    }


    public boolean addPiece(Piece piece, int row, int col)
    {
        if (piecesByPosition[row][col] == null) {
            piecesByPosition[row][col] = piece;
            return true;
        } else {
            return false;
        }
    }

    public GameState build()
    {
        return new GameState(startingPlayer, board, piecesByPosition);
    }

    public Board board()
    {
        return this.board;
    }

    public Player startingPlayer()
    {
        return this.startingPlayer;
    }

    public Piece pieceAt(int row, int col)
    {
        return this.piecesByPosition[row][col];
    }
}
