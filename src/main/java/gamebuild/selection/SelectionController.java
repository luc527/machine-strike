package gamebuild.selection;

import assets.Boards;
import gamebuild.placement.IPiecePlacementController;
import gamebuild.GameBuilder;
import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class SelectionController implements ISelectionController

{
    private final GameBuilder builder;
    private final List<ISelectionObserver> os;
    private final IPiecePlacementController nextCon;

    public SelectionController(GameBuilder builder, IPiecePlacementController nextCon)
    {
        this.builder = builder;
        this.nextCon = nextCon;
        os = new ArrayList<>();
    }

    @Override
    public List<Board> getBoardList()
    {
        return Boards.all();
    }

    @Override
    public void attach(ISelectionObserver obs)
    {
        os.add(obs);
    }


    @Override
    public void select(Player startingPlayer, Board board)
    {
        builder.setStartingPlayer(startingPlayer);
        builder.setBoard(board);
        os.forEach(o -> o.selectionFinished(startingPlayer, board, nextCon));
    }
}
