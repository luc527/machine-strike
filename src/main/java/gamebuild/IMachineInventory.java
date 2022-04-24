package gamebuild;

import java.util.Set;

// Read-only interface

public interface IMachineInventory
{
    Set<String> getMachines();

    int getAmount(String machine);

    boolean isEmpty();
}
