package gameplay;

import logic.Board;
import logic.Coord;
import logic.Direction;
import logic.IPiece;
import logic.turn.ConflictResult;

public interface IGameController
{
    //

    Board getBoard();

    IPiece pieceAt(Coord coord);

    ConflictResult conflict(Coord atkCoord, Coord defCoord, IPiece atkPiece, IPiece defPiece, Direction atkDirection);

    //

    void startGame();

    void attach(GameObserver observer);

    boolean selectPiece(int row, int col);

    boolean unselectPiece();

    boolean performMovement(int row, int col, Direction dir, boolean thenAttack);

    boolean finishTurn();
}
