package gameplay;

import components.boardgrid.BoardGridModel;
import logic.Player;

public interface GameObserver
{
    void start(Player firstPlayer, BoardGridModel board);
}
