package gamebuild.selection;

import logic.Board;
import logic.Player;

import java.util.List;

public interface ISelectionController
{
    List<Board> getBoardList();

    void attach(ISelectionObserver obs);

    void select(Player startingPlayer, Board board);
}
