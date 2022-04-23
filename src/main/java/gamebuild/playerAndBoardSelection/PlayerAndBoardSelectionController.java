package gamebuild.playerAndBoardSelection;

import assets.Boards;
import gamebuild.machineSelection.IMachineSelectionController;
import gamebuild.GameBuilder;
import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PlayerAndBoardSelectionController implements IPlayerAndBoardSelectionController
{
    public interface NextControllerConstructor extends Supplier<IMachineSelectionController>
    {};

    private final GameBuilder builder;
    private final List<IPlayerAndBoardSelectionObserver> os;
    private final NextControllerConstructor next;

    public PlayerAndBoardSelectionController(GameBuilder builder, NextControllerConstructor next)
    {
        this.builder = builder;
        this.next = next;
        os = new ArrayList<>();
    }

    @Override
    public List<Board> getBoardList()
    {
        return Boards.all();
    }

    @Override
    public void attach(IPlayerAndBoardSelectionObserver obs)
    {
        os.add(obs);
    }


    @Override
    public void select(Player startingPlayer, Board board)
    {
        builder.setStartingPlayer(startingPlayer);
        builder.setBoard(board);
        var nextCon = next.get();
        os.forEach(o -> o.selectionFinished(startingPlayer, board, nextCon));
    }
}
