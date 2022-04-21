package gamebuild_old;

import logic.Board;
import logic.Player;

// :PatternUsed Observer

public interface GameSelectionObserver
{
    void selectedStartingPlayer(Player p);

    void selectedBoard(Board b);

    void selectionsConfirmed();
}
