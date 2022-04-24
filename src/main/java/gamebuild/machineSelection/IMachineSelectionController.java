package gamebuild.machineSelection;

import logic.Player;

public interface IMachineSelectionController
{
    void attach(IMachineSelectionObserver obs);

    Player getFirstPlayer();

    IPlayerMachineSelectionModel getPlayerMachineSelection(Player player);

    void selectMachine(Player player, String machine);

    void deselectMachine(Player player, String machine);

    void playerDone(Player player);
}
