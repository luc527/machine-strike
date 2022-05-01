package gamebuild.machineSelection;

import gamebuild.GameBuilder;
import gamebuild.piecePlacement.PiecePlacementController;
import logic.Machine;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class MachineSelectionController implements IMachineSelectionController
{
    private final GameBuilder gameBuilder;
    private final PlayerMachineSelectionModel p1machineSelection;
    private final PlayerMachineSelectionModel p2machineSelection;
    private final List<IMachineSelectionObserver> observers;

    public MachineSelectionController(GameBuilder gameBuilder)
    {
        this.gameBuilder = gameBuilder;
        p1machineSelection = new PlayerMachineSelectionModel();
        p2machineSelection = new PlayerMachineSelectionModel();
        observers = new ArrayList<>();
    }

    @Override
    public void attach(IMachineSelectionObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public Player getFirstPlayer()
    {
        return gameBuilder.startingPlayer();
    }

    @Override
    public PlayerMachineSelectionModel getPlayerMachineSelection(Player p)
    {
        return p == Player.PLAYER1 ? p1machineSelection : p2machineSelection;
    }

    @Override
    public void selectMachine(Player player, Machine machine)
    {
        var response = getPlayerMachineSelection(player).select(machine);
        observers.forEach(o -> o.acceptSelectionResponse(player, machine, response));
    }

    @Override
    public void deselectMachine(Player player, Machine machine)
    {
        var response = getPlayerMachineSelection(player).deselect(machine);
        observers.forEach(o -> o.acceptDeselectionResponse(player, machine, response));
    }

    @Override
    public void playerDone(Player player)
    {
        if (player == gameBuilder.startingPlayer()) {
            observers.forEach(o -> o.switchToPlayer(player.next()));
        } else {
            var p1inv = p1machineSelection.selectedInventory();
            var p2inv = p2machineSelection.selectedInventory();
            var con = new PiecePlacementController(gameBuilder, p1inv, p2inv);
            observers.forEach(o -> o.selectionFinished(p1inv, p2inv, con));
        }
    }
}
