package logic;

public class GameState
{
    private Player currentPlayer;
    private final Board board;
    private final Piece[][] pieces;

    public GameState(Player startingPlayer, Board board, Piece[][] startingPieces)
    {
        this.currentPlayer = startingPlayer;
        this.board = board;
        var height = board.height();
        var width = board.width();
        this.pieces = new Piece[height][width];
        for (var i = 0; i < height; i++)
            for (var j = 0; j < width; j++)
                this.pieces[i][j] = startingPieces[i][j];
    }

}
