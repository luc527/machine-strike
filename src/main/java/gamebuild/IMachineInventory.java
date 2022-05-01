package gamebuild;

import logic.Machine;

import java.util.List;

// Read-only interface

public interface IMachineInventory
{
    List<Machine> getMachines();

    int getAmount(Machine machine);

    boolean isEmpty();
}
