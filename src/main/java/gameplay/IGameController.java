package gameplay;

public interface IGameController
{
    void attach(GameObserver observer);

    void startGame();

    PieceSelectionResponse selectPiece();
}
