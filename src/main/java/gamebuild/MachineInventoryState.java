package gamebuild;

import assets.DefaultMachineInventory;
import assets.Machines;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MachineInventoryState implements IMachineInventory
{
    private final Map<String, Integer> machineAmounts;

    private MachineInventoryState()
    {
        this.machineAmounts = new HashMap<>();
    }

    @Override
    public Set<String> getMachines()
    {
        return machineAmounts.keySet();
    }

    @Override
    public int getAmount(String machine)
    {
        return machineAmounts.getOrDefault(machine, 0);
    }

    public void add(String m)
    {
        add(m, 1);
    }

    public void add(String m, int amount)
    {
        machineAmounts.put(m, amount + getAmount(m));
    }

    public boolean take(String m)
    {
        var amountLeft = getAmount(m) - 1;
        if (amountLeft < 0) return false;
        machineAmounts.put(m, amountLeft);
        return true;
    }

    public static MachineInventoryState initial()
    {
        var inv = new MachineInventoryState();
        for (var machine : Machines.all()) {
            var name = machine.name();
            inv.add(name, DefaultMachineInventory.get(name));
        }
        return inv;
    }
}
