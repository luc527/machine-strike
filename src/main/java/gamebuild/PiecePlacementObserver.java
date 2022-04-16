package gamebuild;

import logic.Coord;
import logic.Player;

// :PatternUsed Observer

public interface PiecePlacementObserver
{
    void machineCursorSetTo(String machineName);

    void machineSelected(String machineName);

    void placingPlayerSwitchedTo(Player placingPlayer);

    void boardCursorMovedOver(Coord coord);

    void currentPlacementCancelled();

    void currentPlacementConfirmed();
}
