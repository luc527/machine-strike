package logic.attackType;

import logic.*;

import java.util.List;

public class PullMachineType extends MachineType
{
    public PullMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(game, from, piece, dir); }

    @Override
    public boolean knockbackOnEqualCombatPower() { return false; }

    @Override
    public int combatPowerOffset(Terrain terrain)
    { return terrain.equals(Terrain.MARSH) ? 1 : terrain.combatPowerOffset(); }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) return MovResponse.NO_ATTACKED_PIECE_IN_RANGE;
        var defCoord = defCoordList.get(0);
        var res = performBasicAttack(game, atkCoord, atkDirection, defCoord);
        if (res != MovResponse.OK) return res;

        var defPiece = game.pieceAt(defCoord);
        if (!defPiece.dead()) {
            // Pull attacked piece
            game.movePiece(defCoord, atkCoord.moved(atkDirection));
        }
        return MovResponse.OK;
    }

    @Override
    public String name()
    { return "Pull"; }
}
