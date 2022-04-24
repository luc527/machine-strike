package gamebuild.playerAndBoardSelection;

import assets.Boards;
import gamebuild.GameBuilder;
import gamebuild.machineSelection.MachineSelectionController;
import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAndBoardSelectionController implements IPlayerAndBoardSelectionController
{
    private final GameBuilder gameBuilder;
    private final List<IPlayerAndBoardSelectionObserver> observers;

    public PlayerAndBoardSelectionController(GameBuilder gameBuilder)
    {
        this.gameBuilder = gameBuilder;
        observers = new ArrayList<>();
    }

    @Override
    public List<Board> getBoardList()
    {
        return Boards.all();
    }

    @Override
    public void attach(IPlayerAndBoardSelectionObserver observer)
    {
        observers.add(observer);
    }


    @Override
    public void select(Player startingPlayer, Board board)
    {
        gameBuilder.setStartingPlayer(startingPlayer);
        gameBuilder.setBoard(board);
        var nextController = new MachineSelectionController(gameBuilder);
        observers.forEach(o -> o.selectionFinished(startingPlayer, board, nextController));
    }
}
