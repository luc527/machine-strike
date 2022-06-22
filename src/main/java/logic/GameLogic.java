package logic;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class GameLogic
{

    public static Set<Coord> generateAvailablePositions(int sourceRow, int sourceCol, int movementRange)
    {
        if (movementRange < 1) return Set.of(Coord.create(sourceRow, sourceCol));
        var set = new HashSet<Coord>();

        BiConsumer<Integer, Integer> makeRow = (blanks, row) -> {
            var col = sourceCol - movementRange;
            for (var j=0; j<blanks; j++) col++;
            for (var j=0; j<2*movementRange+1-2*blanks; j++) set.add(Coord.create(row, col++));
        };

        var row = sourceRow - movementRange;
        for (var blanks = movementRange; blanks >= 0; blanks--) {
            makeRow.accept(blanks, row++);
        }
        for (var blanks = 1; blanks <= movementRange; blanks++) {
            makeRow.accept(blanks, row++);
        }

        return set;
    }

    public static int combatPower(Machine machine, Terrain terrain, Direction direction)
    {
        return combatPower(machine, terrain) + machine.point(direction).combatPowerOffset();
    }

    public static int combatPower(Machine machine, Terrain terrain)
    {
        return machine.attackPower() + terrain.combatPowerOffset();
    }

    public static ConflictDamage conflictDamage(
            Machine atkMachine, Direction atkDirection, Terrain atkTerrain,
            Machine defMachine, Terrain defTerrain
    ) {
        var defDirection = atkDirection.opposite();
        var atkPower = combatPower(atkMachine, atkTerrain, atkDirection);
        var defPower = combatPower(defMachine, defTerrain, defDirection) - defMachine.attackPower();
        var diff = atkPower - defPower;
        // TODO if equal (diff 0) then break armor
        var atkDamage = diff < 0 ? -diff : 0;
        var defDamage = diff < 0 ? 0 : diff;
        return new ConflictDamage(atkDamage, defDamage);
    }

}
