package gamebuild;

import logic.Player;

// :PatternUsed Observer

public interface PiecePlacementObserver
{
    void setInitialPlacingPlayer(Player placingPlayer);

    void machineCursorSetTo(String machineName);

    void placingPlayerSwitchedTo(Player placingPlayer);

    void machineUnderCursorSelected();
}
