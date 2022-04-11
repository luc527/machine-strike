package gameconfig;

import logic.*;

import java.util.EnumMap;
import java.util.List;

// Pattern: Builder
// Builds an initial game state

public class GameConfiguration
{
    private Player startingPlayer;
    private Board board;
    private Piece[][] piecesByPosition;

    public GameConfiguration()
    {
        this.startingPlayer = null;
        this.board = null;
        this.piecesByPosition = new Piece[Constants.BOARD_HEIGHT][Constants.BOARD_WIDTH];
    }

    public void setStartingPlayer(Player p) {this.startingPlayer = p;}
    public Player startingPlayer() {return this.startingPlayer;}

    public void setBoard(Board b) {this.board = b;}
    public Board board() {return this.board;}

    public Piece addPiece(Player player, Machine machine, Direction dir, int row, int col)
    {
        var piece = new Piece(machine, dir, player);
        piecesByPosition[row][col] = piece;
        return piece;
    }

    public Piece pieceAt(int row, int col) {return this.piecesByPosition[row][col];}

    public GameState initialGameState()
    {
        return new GameState(startingPlayer, board, piecesByPosition);
    }
}
