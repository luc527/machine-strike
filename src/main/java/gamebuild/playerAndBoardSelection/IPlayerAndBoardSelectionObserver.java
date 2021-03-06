package gamebuild.playerAndBoardSelection;

import gamebuild.machineSelection.IMachineSelectionController;
import logic.Board;
import logic.Player;

public interface IPlayerAndBoardSelectionObserver
{
    void selectionFinished(Player startingPlayer, Board board, IMachineSelectionController nextCon);
}
