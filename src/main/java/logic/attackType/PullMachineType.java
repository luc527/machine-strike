package logic.attackType;

import logic.*;

import java.util.List;

public class PullMachineType extends MachineType
{
    public PullMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(pieceAt, from, piece, dir); }

    @Override
    public boolean knockbackOnEqualCombatPower() { return false; }

    @Override
    public int combatPowerOffset(Terrain terrain)
    { return terrain.equals(Terrain.MARSH) ? 1 : terrain.combatPowerOffset(); }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var result = performBasicAttack(game, atkCoord, atkDirection);
        if (!result.success()) return result;

        var defCoord = result.defendingPieceCoords().get(0);
        var defFinalCoord = defCoord;

        var defPiece = result.defendingPieces().get(0);
        if (!defPiece.dead()) {
            // Pull attacked piece
            var pulledCoord = atkCoord.moved(atkDirection);
            var terrain = game.board().get(pulledCoord);
            if (defPiece.machine().type().walksOn(terrain)) {
                game.movePiece(defCoord, pulledCoord);
                defFinalCoord = pulledCoord;
            }
        }
        return new MovResult(atkCoord, List.of(defFinalCoord), result.defendingPieces());
    }

    @Override
    public String name()
    { return "Pull"; }
}
