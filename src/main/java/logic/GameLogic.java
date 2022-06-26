package logic;

import constants.Constants;
import logic.turn.ConflictDamage;

public class GameLogic
{

    public static Reachability reachability(Coord center, Coord coord, int movementRange)
    {
        var dist = Math.abs(center.row() - coord.row()) + Math.abs(center.col() - coord.col());
        if (dist > movementRange + 1) return Reachability.OUT;
        if (dist == movementRange + 1) return Reachability.IN_RUNNING;
        return Reachability.IN;
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

        // TODO verify calculations...

        var defDirection = atkDirection.opposite();
        var atkPower = combatPower(atkMachine, atkTerrain, atkDirection);
        var defPower = combatPower(defMachine, defTerrain, defDirection) - defMachine.attackPower();
        var diff = atkPower - defPower;

        var atkDamage = 0;
        var defDamage = 0;
        var breakArmor = false;

        if (diff < 0) {
            atkDamage = -diff;
        } else if (diff > 0) {
            defDamage = diff;
        } else {
            breakArmor = true;
            atkDamage = -1;
            defDamage = -1;
        }
        return new ConflictDamage(atkDamage, defDamage, breakArmor);
    }

    public static boolean inbounds(Coord coord)
    {
        return inbounds(coord.row(), coord.col());
    }

    public static boolean inbounds(int row, int col)
    {
        return row >= 0 && row < Constants.BOARD_ROWS
            && col >= 0 && col < Constants.BOARD_COLS;
    }
}
