package logic.attackType;

public class MachineTypeFactory
{
    public static MachineType make(String type, int attackRange)
    {
        switch (type) {
            case "melee" -> { return new MeleeMachineType(attackRange); }
            case "gunner" -> { return new GunnerMachineType(attackRange); }
            case "ram" -> { return new RamMachineType(attackRange); }
            case "dash" -> { return new DashMachineType(attackRange); }
            default -> { return new MeleeMachineType(attackRange); }
            // TODO RuntimeException for unsupported types
        }
    }
}
