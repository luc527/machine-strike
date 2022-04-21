package gamebuild_old;

import logic.CoordState;

// :PatternUsed Observer

public interface PiecePlacementObserver
{
    void machineCursorOver(String machineName);

    void machineSelected(String machineName);

    void boardCursorOver(CoordState coord);

    void currentPlacementCancelled();

    void currentPlacementConfirmed();

    void currentPlacementFailed();
}
