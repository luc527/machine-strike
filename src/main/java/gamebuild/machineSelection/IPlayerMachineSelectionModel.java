package gamebuild.machineSelection;

// Read-only interface

public interface IPlayerMachineSelectionModel
{
    int availableAmount(String machine);

    int selectedAmount(String machine);

    int maxVictoryPoints();

    int currentVictoryPoints();
}
