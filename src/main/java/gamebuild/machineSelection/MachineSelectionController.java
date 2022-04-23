package gamebuild.machineSelection;

import gamebuild.GameBuilder;
import gamebuild.MachineInventory;
import gamebuild.placement.IPiecePlacementController;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

// TODO instead of getFirstPlayer, make the observers implement start(Player)
//  although idk how that's gonna fit with the view starting/showing and so on

public class MachineSelectionController implements IMachineSelectionController
{
    private final GameBuilder builder;
    private final NextControllerConstructor consNext;
    private final PlayerMachineSelectionModel p1machSelModel;
    private final PlayerMachineSelectionModel p2machSelModel;
    private final List<IMachineSelectionObserver> os;

    // Shortcut
    public interface NextControllerConstructor
    extends BiFunction<MachineInventory, MachineInventory, IPiecePlacementController>
    {};

    public MachineSelectionController(GameBuilder builder, NextControllerConstructor consNext)
    {
        this.builder = builder;
        this.consNext = consNext;
        p1machSelModel = new PlayerMachineSelectionModel();
        p2machSelModel = new PlayerMachineSelectionModel();
        os = new ArrayList<>();
    }

    @Override
    public void attach(IMachineSelectionObserver obs)
    {
        os.add(obs);
    }

    @Override
    public Player getFirstPlayer()
    {
        return builder.startingPlayer();
    }

    @Override
    public PlayerMachineSelectionModel playerMachineSelectionModel(Player p)
    {
        return p == Player.PLAYER1 ? p1machSelModel : p2machSelModel;
    }

    @Override
    public void selectMachine(Player player, String machine)
    {
        var response = playerMachineSelectionModel(player).select(machine);
        os.forEach(o -> o.acceptSelectionResponse(player, machine, response));
    }

    @Override
    public void deselectMachine(Player player, String machine)
    {
        var response = playerMachineSelectionModel(player).deselect(machine);
        os.forEach(o -> o.acceptDeselectionResponse(player, machine, response));
    }

    @Override
    public void playerDone(Player player)
    {
        if (player == builder.startingPlayer()) {
            os.forEach(o -> o.switchToPlayer(player.next()));
        } else {
            var p1inv = p1machSelModel.selectedInventory();
            var p2inv = p2machSelModel.selectedInventory();
            os.forEach(o -> o.selectionFinished(p1inv, p2inv, consNext.apply(p1inv, p2inv)));
        }
    }
}
