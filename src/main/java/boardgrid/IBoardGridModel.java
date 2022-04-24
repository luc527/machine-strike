package boardgrid;

import logic.Coord;
import logic.Direction;

import java.util.Set;

public interface IBoardGridModel
{
    void startInteraction(Coord cursor, Set<Coord> availablePositions, String carriedMachine);

    void endInteraction();

    Coord getCursor();

    String getCarriedMachine();

    void iterate(BoardGridModel.BoardGridIterator it);

    void moveCursor(Direction dir);

}
