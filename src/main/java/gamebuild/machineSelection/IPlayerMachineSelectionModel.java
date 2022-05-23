package gamebuild.machineSelection;

// Read-only interface

import logic.Machine;

public interface IPlayerMachineSelectionModel
{
    int availableAmount(Machine machine);

    int selectedAmount(Machine machine);

    boolean selectable(Machine machine);

    int maxVictoryPoints();

    int currentVictoryPoints();

    boolean isSelectionEmpty();
}
