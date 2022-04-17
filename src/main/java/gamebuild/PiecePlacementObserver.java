package gamebuild;

import logic.Coord;
import logic.Player;

// :PatternUsed Observer

public interface PiecePlacementObserver
{
    void machineCursorSetTo(String machineName);

    void machineSelected(String machineName);

    void boardCursorMovedOver(Coord coord);

    void currentPlacementCancelled();

    void currentPlacementConfirmed();

    void currentPlacementFailed();
}
