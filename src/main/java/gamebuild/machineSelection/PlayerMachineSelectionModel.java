package gamebuild.machineSelection;

import assets.Machines;
import gamebuild.IMachineInventory;
import gamebuild.MachineInventory;

public class PlayerMachineSelectionModel implements IPlayerMachineSelectionModel
{
    private final static int MAX_VICTORY_POINTS = 10;

    private final IMachineInventory availableInv         = MachineInventory.full();
    private final  MachineInventory selectedInv          = MachineInventory.empty();
    private                     int currentVictoryPoints = 0;

    public MachineInventory selectedInventory()
    { return selectedInv; }

    @Override
    public int availableAmount(String machine)
    { return availableInv.getAmount(machine); }

    @Override
    public int selectedAmount(String machine)
    { return selectedInv.getAmount(machine); }

    @Override
    public int maxVictoryPoints()
    { return MAX_VICTORY_POINTS; }

    @Override
    public int currentVictoryPoints()
    { return currentVictoryPoints; }

    public MachineSelectionResponse select(String machine)
    {
        if (selectedAmount(machine) == availableAmount(machine)) {
            return MachineSelectionResponse.UNAVAILABLE_AMOUNT;
        }

        var vp = Machines.get(machine).victoryPoints();
        if (vp + currentVictoryPoints > MAX_VICTORY_POINTS) {
            return MachineSelectionResponse.UNAVAILABLE_VICTORY_POINTS;
        }

        currentVictoryPoints += vp;
        selectedInv.add(machine);
        return MachineSelectionResponse.SELECTED;
    }

    public MachineDeselectionResponse deselect(String machine)
    {
        if (selectedAmount(machine) <= 0) {
            return MachineDeselectionResponse.NONE_SELECTED;
        }
        currentVictoryPoints -= Machines.get(machine).victoryPoints();
        selectedInv.take(machine);
        return MachineDeselectionResponse.DESELECTED;
    }

}
