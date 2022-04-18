package gamebuild;

import logic.Coord;

// :PatternUsed Observer

public interface PiecePlacementObserver
{
    void machineCursorOver(String machineName);

    void machineSelected(String machineName);

    void boardCursorOver(Coord coord);

    void currentPlacementCancelled();

    void currentPlacementConfirmed();

    void currentPlacementFailed();
}
