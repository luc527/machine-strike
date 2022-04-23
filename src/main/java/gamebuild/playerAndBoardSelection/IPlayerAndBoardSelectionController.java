package gamebuild.playerAndBoardSelection;

import logic.Board;
import logic.Player;

import java.util.List;

public interface IPlayerAndBoardSelectionController
{
    List<Board> getBoardList();

    void attach(IPlayerAndBoardSelectionObserver obs);

    void select(Player startingPlayer, Board board);
}
