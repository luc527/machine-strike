package gameplay;

import logic.Board;
import logic.Coord;
import logic.Direction;
import logic.Piece;

public interface IGameController
{
    //

    Board getBoard();

    Piece pieceAt(Coord coord);

    //

    void startGame();

    void attach(GameObserver observer);

    boolean selectPiece(int row, int col);

    boolean unselectPiece();

    boolean placePiece(int row, int col, Direction dir);
}
