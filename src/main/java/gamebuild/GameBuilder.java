package gamebuild;

import logic.*;
import utils.Constants;

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

    public GameState initialGameState()
    {
        return new GameState(startingPlayer, board, piecesByPosition);
    }
}
