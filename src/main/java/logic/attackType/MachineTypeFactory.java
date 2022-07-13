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
            case "swoop" -> { return new SwoopMachineType(attackRange); }
            case "pull" -> { return new PullMachineType(attackRange); }
        }
        throw new RuntimeException("Unknown machine type '"+type+"'");
    }
}
