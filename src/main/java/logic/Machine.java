package logic;

import com.google.gson.JsonElement;
import logic.attackType.MachineType;
import logic.attackType.MachineTypeFactory;

import java.util.Arrays;
import java.util.Objects;

// :PatternUsed? Prototype (for Piece)

public class Machine
{
    public enum Point
    {
        ARMORED(1),
        WEAK(-1),
        EMPTY(0);

        private int combatPowerOffset;

        Point(int combatPowerOffset) {
            this.combatPowerOffset = combatPowerOffset;
        }

        public int combatPowerOffset() {
            return this.combatPowerOffset;
        }

        public static Point from(String s) {
            switch(s) {
                case "armored": return ARMORED;
                case "weak": return WEAK;
                case "empty": return EMPTY;
            }
            throw new RuntimeException("Invalid machine point name: " + s);
        }
    }

    private final MachineType machineType;
    private final String name;
    private final int attackPower;
    private final int attackRange;
    private final int movementRange;
    private final int health;
    private final int victoryPoints;
    private final Point[] points = new Point[4];

    public Machine(MachineType machineType, String name, int attackPower, int attackRange, int movementRange, int victoryPoints, int health, Point[] points)
    {
        this.machineType = machineType;
        this.name = name;
        this.attackPower = attackPower;
        this.attackRange = attackRange;
        this.movementRange = movementRange;
        this.health = health;
        this.victoryPoints = victoryPoints;
        this.points[Direction.NORTH.idx()] = points[Direction.NORTH.idx()];
        this.points[Direction.EAST.idx()] = points[Direction.EAST.idx()];
        this.points[Direction.SOUTH.idx()] = points[Direction.SOUTH.idx()];
        this.points[Direction.WEST.idx()] = points[Direction.WEST.idx()];
    }

    public String name() { return this.name; }
    public int attackPower() { return this.attackPower; }
    public int attackRange() { return this.attackRange; }
    public int movementRange() { return this.movementRange; }
    public int victoryPoints() { return this.victoryPoints; }
    public int health() { return this.health; }
    public Point point(Direction dir) { return this.points[dir.idx()]; }
    public MachineType type() { return this.machineType; }

    public static Machine fromJsonElement(JsonElement json)
    {
        var obj = json.getAsJsonObject();
        var name = obj.get("name").getAsJsonPrimitive().getAsString();
        var attackPower = obj.get("attackPower").getAsJsonPrimitive().getAsInt();
        var attackRange = obj.get("attackRange").getAsJsonPrimitive().getAsInt();
        var movementRange = obj.get("movementRange").getAsJsonPrimitive().getAsInt();
        var victoryPoints = obj.get("victoryPoints").getAsJsonPrimitive().getAsInt();
        var health = obj.get("health").getAsJsonPrimitive().getAsInt();

        var typeString = obj.get("type").getAsJsonPrimitive().getAsString();
        var type = MachineTypeFactory.make(typeString, attackRange);

        var pointsObj = obj.get("points").getAsJsonObject();
        var points = new Point[4];
        points[Direction.NORTH.idx()] = Point.from(pointsObj.get("north").getAsJsonPrimitive().getAsString());
        points[Direction.EAST.idx()] = Point.from(pointsObj.get("east").getAsJsonPrimitive().getAsString());
        points[Direction.WEST.idx()] = Point.from(pointsObj.get("west").getAsJsonPrimitive().getAsString());
        points[Direction.SOUTH.idx()] = Point.from(pointsObj.get("south").getAsJsonPrimitive().getAsString());

        return new Machine(type, name, attackPower, attackRange, movementRange, victoryPoints, health, points);
    }

    @Override
    public String toString()
    {
        return "logic.Machine{" +
            "machineType='" + machineType + '\'' +
            ", name='" + name + '\'' +
            ", attackPower=" + attackPower +
            ", attackRange=" + attackRange +
            ", movementRange=" + movementRange +
            ", victoryPoints=" + victoryPoints +
            ", health=" + health +
            ", points=" + Arrays.toString(points) +
            '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return name.equals(machine.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }
}
