package gamebuild.machineSelection;

import logic.Machine;
import logic.Player;

public interface IMachineSelectionController
{
    void attach(IMachineSelectionObserver obs);

    Player getFirstPlayer();

    IPlayerMachineSelectionModel getPlayerMachineSelection(Player player);

    void selectMachine(Player player, Machine machine);

    void deselectMachine(Player player, Machine machine);

    void playerDone(Player player);
}
