package gamebuild;

import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class GameBuildingController
{
    private final GameBuilder game = new GameBuilder();
    private final List<GameSelectionObserver> selecObservers = new ArrayList<>();
    private final List<PiecePlacementObserver> pieceObservers = new ArrayList<>();

    private Player placingPlayer;

    public void attach(GameSelectionObserver obs)
    {
        selecObservers.add(obs);
    }

    public void attach(PiecePlacementObserver obs)
    {
        pieceObservers.add(obs);
        obs.setInitialPlacingPlayer(placingPlayer);
    }

    public void selectStartingPlayer(Player p)
    {
        game.setStartingPlayer(p);
        placingPlayer = p;
        selecObservers.forEach(o -> o.selectedStartingPlayer(p));
    }

    public void selectBoard(Board b)
    {
        game.setBoard(b);
        selecObservers.forEach(o -> o.selectedBoard(b));
    }

    public void confirmSelections()
    {
        selecObservers.forEach(GameSelectionObserver::selectionsConfirmed);
    }

    public void setMachineCursorOver(String machineName)
    {
        pieceObservers.forEach(o -> o.machineCursorSetTo(machineName));
    }

    // switchPlacingPlaye is probably temporary
    // because we automatically switch players once a player places a piece on the board

    public void switchPlacingPlayer()
    {
        this.placingPlayer = placingPlayer.next();
        pieceObservers.forEach(o -> o.placingPlayerSwitchedTo(placingPlayer));
    }

    public void selectMachineUnderCursor()
    {
        pieceObservers.forEach(o -> o.machineUnderCursorSelected());
    }
}
