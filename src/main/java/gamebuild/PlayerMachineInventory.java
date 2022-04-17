package gamebuild;

import assets.MachineInventory;
import assets.Machines;
import logic.Machine;

import java.util.HashMap;
import java.util.Map;

public class PlayerMachineInventory
{
    private final Map<String, Integer> machineAmounts;

    private PlayerMachineInventory()
    {
        this.machineAmounts = new HashMap<>();
    }

    public int amount(String m)
    {
        return machineAmounts.getOrDefault(m, 0);
    }

    public void add(String m)
    {
        add(m, 1);
    }

    public void add(String m, int amount)
    {
        machineAmounts.put(m, amount + amount(m));
    }

    public boolean take(String m)
    {
        var amountLeft = amount(m) - 1;
        if (amountLeft < 0) return false;
        machineAmounts.put(m, amountLeft);
        return true;
    }

    public static PlayerMachineInventory initial()
    {
        var inv = new PlayerMachineInventory();
        for (var machine : Machines.all()) {
            var name = machine.name();
            inv.add(name, MachineInventory.get(name));
        }
        return inv;
    }
}
