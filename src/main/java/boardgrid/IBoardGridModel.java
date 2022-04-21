package boardgrid;

import logic.Direction;
import logic.ICoord;

import java.util.Set;

public interface IBoardGridModel
{
    void startInteraction(ICoord cursor, Set<ICoord> availablePositions, String carriedMachine);

    void endInteraction();

    ICoord getCursor();

    String getCarriedMachine();

    void iterate(BoardGridModel.BoardGridIterator it);

    void moveCursor(Direction dir);

}
