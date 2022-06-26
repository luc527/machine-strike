package gameplay;

import logic.Board;
import logic.Coord;
import logic.Direction;
import logic.IPiece;

public interface IGameController
{
    //

    Board getBoard();

    IPiece pieceAt(Coord coord);

    //

    void startGame();

    void attach(GameObserver observer);

    boolean selectPiece(int row, int col);

    boolean unselectPiece();

    boolean performMovement(int row, int col, Direction dir, boolean thenAttack);

    boolean finishTurn();
}
