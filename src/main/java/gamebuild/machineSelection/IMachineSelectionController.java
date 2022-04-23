package gamebuild.machineSelection;

import logic.Player;

public interface IMachineSelectionController
{
    void attach(IMachineSelectionObserver obs);

    Player getFirstPlayer();

    IPlayerMachineSelectionModel playerMachineSelectionModel(Player player1);

    void selectMachine(Player player, String machine);

    void deselectMachine(Player player, String machine);

    void playerDone(Player player);
}
