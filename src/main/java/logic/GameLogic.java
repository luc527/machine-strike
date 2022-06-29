package logic;

import constants.Constants;
import logic.turn.ConflictResult;

public class GameLogic
{

    public static final int OVERCHARGE_DAMAGE = 2;

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
