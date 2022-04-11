package gameconfig;

import logic.Board;
import logic.Player;

// :PatternUsed Observer

public interface GameConfigurationObserver
{
    void selectedStartingPlayer(Player p);

    void selectedBoard(Board b);

    void selectionsConfirmed();
}
