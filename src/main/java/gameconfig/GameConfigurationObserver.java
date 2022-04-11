package gameconfig;

import logic.Board;
import logic.Player;

public interface GameConfigurationObserver
{
    void selectedStartingPlayer(Player p);

    void selectedBoard(Board b);

    void startingPlayerAndBoardConfirmed();
}
