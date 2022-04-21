package gamebuild.selection;

import assets.Boards;
import gamebuild.placement.IPiecePlacementController;
import gamebuild.GameBuilder;
import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SelectionController implements ISelectionController
{
    public interface NextControllerConstructor extends Function<GameBuilder, IPiecePlacementController> {};

    private final GameBuilder builder;
    private final List<ISelectionObserver> os;
    private final NextControllerConstructor consNextCon;

    public SelectionController(GameBuilder builder, NextControllerConstructor consNextCon)
    {
        this.builder = builder;
        this.consNextCon = consNextCon;
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
        var nextCon = consNextCon.apply(builder);
        os.forEach(o -> o.selectionFinished(startingPlayer, board, nextCon));
    }
}
