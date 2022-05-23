package gamebuild.machineSelection;

import assets.Machines;
import gamebuild.IMachineInventory;
import gamebuild.MachineInventory;
import logic.Machine;

public class PlayerMachineSelectionModel implements IPlayerMachineSelectionModel
{
    private final static int MAX_VICTORY_POINTS = 10;

    private final IMachineInventory availableInv         = MachineInventory.full();
    private final  MachineInventory selectedInv          = MachineInventory.empty();
    private                     int currentVictoryPoints = 0;

    // inventory of selected machines
    public MachineInventory selectedInventory()
    { return selectedInv; }

    @Override
    public int availableAmount(Machine machine)
    { return availableInv.getAmount(machine); }

    @Override
    public int selectedAmount(Machine machine)
    { return selectedInv.getAmount(machine); }

    @Override
    public boolean selectable(Machine machine)
    { return machine.victoryPoints() + currentVictoryPoints() <= maxVictoryPoints(); }

    @Override
    public int maxVictoryPoints()
    { return MAX_VICTORY_POINTS; }

    @Override
    public int currentVictoryPoints()
    { return currentVictoryPoints; }

    @Override
    public boolean isSelectionEmpty()
    { return selectedInv.isEmpty(); }

    public MachineSelectionResponse select(Machine machine)
    {
        if (selectedAmount(machine) == availableAmount(machine)) {
            return MachineSelectionResponse.UNAVAILABLE_AMOUNT;
        }

        var vp = machine.victoryPoints();
        if (vp + currentVictoryPoints > MAX_VICTORY_POINTS) {
            return MachineSelectionResponse.UNAVAILABLE_VICTORY_POINTS;
        }

        currentVictoryPoints += vp;
        selectedInv.add(machine);
        return MachineSelectionResponse.SELECTED;
    }

    public MachineDeselectionResponse deselect(Machine machine)
    {
        if (selectedAmount(machine) <= 0) {
            return MachineDeselectionResponse.NONE_SELECTED;
        }
        currentVictoryPoints -= machine.victoryPoints();
        selectedInv.take(machine);
        return MachineDeselectionResponse.DESELECTED;
    }

}
