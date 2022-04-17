package gamebuild;

import logic.Board;
import logic.Coord;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

// TODO refactor into two controllers, one for "game selection" and another for "piece placement"

public class GameBuildingController
{
    private final GameBuilder gameBuilder = new GameBuilder();
    private final List<GameSelectionObserver> selecObservers = new ArrayList<>();
    private final List<PiecePlacementObserver> pieceObservers = new ArrayList<>();

    private Player placingPlayer;
    private final PlayerMachineInventory p1inventory = PlayerMachineInventory.initial();
    private final PlayerMachineInventory p2inventory = PlayerMachineInventory.initial();
    private String selectedMachine;

    public void attach(GameSelectionObserver obs)
    {
        selecObservers.add(obs);
    }

    public void attach(PiecePlacementObserver obs)
    {
        pieceObservers.add(obs);
    }

    public void selectBoard(Board b)
    {
        gameBuilder.setBoard(b);
        selecObservers.forEach(o -> o.selectedBoard(b));
    }

    public Board getBoard()
    {
        return gameBuilder.board();
    }

    public void selectStartingPlayer(Player p)
    {
        gameBuilder.setStartingPlayer(p);
        placingPlayer = p;
        selecObservers.forEach(o -> o.selectedStartingPlayer(p));
    }

    public Player getStartingPlayer()
    {
        return gameBuilder.startingPlayer();
    }

    public void confirmSelections()
    {
        selecObservers.forEach(o -> o.selectionsConfirmed());
    }

    public Player getPlacingPlayer()
    {
        return this.placingPlayer;
    }

    public void setMachineCursorOver(String machineName)
    {
        pieceObservers.forEach(o -> o.machineCursorSetTo(machineName));
    }

    public PlayerMachineInventory getPlayerInventory(Player p)
    {
        return p == Player.PLAYER1 ? p1inventory : p2inventory;
    }

    public boolean selectMachine(String machineName)
    {
        this.selectedMachine = machineName;
        if (getPlayerInventory(placingPlayer).amount(machineName) > 0) {
            pieceObservers.forEach(o -> o.machineSelected(machineName));
            return true;
        } else {
            return false;
        }
    }

    public void setBoardCursor(Coord coord)
    {
        pieceObservers.forEach(o -> o.boardCursorMovedOver(coord));
    }

    public void cancelCurrentPlacement()
    {
        pieceObservers.forEach(o -> o.currentPlacementCancelled());
    }

    public void confirmCurrentPlacement()
    {
        // TODO should also signal the end of the placement phase
        getPlayerInventory(placingPlayer).take(selectedMachine);
        selectedMachine = null;
        placingPlayer = placingPlayer.next();
        pieceObservers.forEach(o -> o.currentPlacementConfirmed());
    }
}
