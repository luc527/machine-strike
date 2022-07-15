package gameplay;

import logic.*;

public interface IGameController
{
    void attach(GameObserver observer);

    IGameState getGameState();

    void startGame();

    boolean selectPiece(int row, int col);

    void unselectPiece();

    void performMovement(int row, int col, Direction dir);

    boolean performAttack(int row, int col, Direction dir);

    void finishTurn();

}
