package gameplay;

import logic.Coord;
import logic.Piece;
import logic.Player;

import java.util.Set;

public interface GameObserver
{
    void start(Player firstPlayer);

    void pieceSelected(int row, int col, Piece piece, Set<Coord> availablePositions);

    void pieceUnselected();
}
