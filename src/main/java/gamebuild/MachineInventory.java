package gamebuild;

import assets.DefaultMachineInventory;
import assets.Machines;
import logic.Machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineInventory implements IMachineInventory
{
    private final Map<Machine, Integer> machineAmounts;

    private MachineInventory()
    {
        this.machineAmounts = new HashMap<>();
    }

    @Override
    public List<Machine> getMachines()
    {
        return machineAmounts.keySet().stream().toList();
    }

    @Override
    public int getAmount(Machine machine)
    {
        return machineAmounts.getOrDefault(machine, 0);
    }

    @Override
    public boolean isEmpty()
    {
        var sum = 0;
        for (var machine : getMachines()) {
            sum += getAmount(machine);
        }
        return sum == 0;
    }

    public void add(Machine m)
    {
        add(m, 1);
    }

    public void add(Machine m, int amount)
    {
        machineAmounts.put(m, amount + getAmount(m));
    }

    public void take(Machine m)
    {
        var amountLeft = getAmount(m) - 1;
        if (amountLeft < 0) {
            throw new RuntimeException("Attempted to take a machine with no amount left!");
        }

        if (amountLeft == 0) {
            machineAmounts.remove(m);
        } else {
            machineAmounts.put(m, amountLeft);
        }
    }

    public static MachineInventory full()
    {
        var inv = new MachineInventory();
        for (var machine : Machines.all()) {
            inv.add(machine, DefaultMachineInventory.get(machine));
        }
        return inv;
    }

    public static MachineInventory empty()
    {
        return new MachineInventory();
    }
}
