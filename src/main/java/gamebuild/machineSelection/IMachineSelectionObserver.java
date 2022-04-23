package gamebuild.machineSelection;

import gamebuild.MachineInventory;
import gamebuild.placement.IPiecePlacementController;
import logic.Player;

public interface IMachineSelectionObserver
{
    void selectionFinished(MachineInventory p1inventory, MachineInventory p2inventory, IPiecePlacementController nextCon);

    void acceptDeselectionResponse(Player player, String machine, MachineDeselectionResponse response);

    void acceptSelectionResponse(Player player, String machine, MachineSelectionResponse response);

    void switchToPlayer(Player player);
}
