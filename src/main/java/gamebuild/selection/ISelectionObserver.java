package gamebuild.selection;

import gamebuild.placement.IPiecePlacementController;
import logic.Board;
import logic.Player;

public interface ISelectionObserver
{
    void selectionFinished(Player startingPlayer, Board board, IPiecePlacementController nextCon);
}
