package gameplay;

import logic.*;

import java.util.function.Function;

public interface GameObserver
{
    void start(Player firstPlayer);

    void pieceSelected(int row, int col, IPiece piece, Function<Coord, Reachability> isReachable);

    void movementPerformed(int row, int col, IPiece piece);

    void pieceUnselected();

    void turnFinished(Player nextPlayer);

    void gameWonBy(Player winner);
}
