package gameplay;

import logic.Board;
import logic.Coord;
import logic.Piece;

public interface IGameController
{
    //

    Board getBoard();

    Piece pieceAt(Coord coord);

    //

    void startGame();

    void attach(GameObserver observer);

    void selectPiece(int row, int col);

    void unselectPiece();
}
