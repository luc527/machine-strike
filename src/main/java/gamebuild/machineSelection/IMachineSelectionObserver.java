package gamebuild.machineSelection;

import gamebuild.MachineInventory;
import gamebuild.piecePlacement.IPiecePlacementController;
import logic.Machine;
import logic.Player;

public interface IMachineSelectionObserver
{
    void acceptSelectionResponse(Player player, Machine machine, MachineSelectionResponse response);

    void acceptDeselectionResponse(Player player, Machine machine, MachineDeselectionResponse response);

    void switchToPlayer(Player player);

    void selectionFinished(MachineInventory p1inventory, MachineInventory p2inventory, IPiecePlacementController nextCon);
}
