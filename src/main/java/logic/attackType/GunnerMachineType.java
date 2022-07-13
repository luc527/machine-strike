package logic.attackType;

import logic.*;

import java.util.List;

public class GunnerMachineType extends MachineType
{
    public GunnerMachineType(int attackRange)
    {
        super(attackRange);
    }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return lastInAttackRange(game, from, piece, dir); }

    @Override
    public boolean attacksFriends()
    { return false; }

    @Override
    public int getAttackingPieceDamage(IGameState game, int diff)
    { return diff == 0 ? 1 : super.getAttackingPieceDamage(game, diff); }

    @Override
    public int getDefendingPieceDamage(IGameState game, int diff)
    { return diff == 0 ? 1 : super.getDefendingPieceDamage(game, diff); }

    @Override
    public String toString()
    { return "GunnerMachineType("+attackRange+")"; }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        // This implementation is the same as in MeleeMachineType

        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            // See MeleeMachineType
            return MovResponse.ATK_EMPTY;
        }
        var defCoord = defCoordList.get(0);
        var defPiece = game.pieceAt(defCoord);
        var atkPiece = game.getPiece(atkCoord);

        var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);
        game.dealDamage(atkCoord, getAttackingPieceDamage(game, combatPowerDiff));
        game.dealDamage(defCoord, getDefendingPieceDamage(game, combatPowerDiff));

        // Knockback
        if (!defPiece.dead() && combatPowerDiff == 0) {
            game.movePiece(defCoord, defCoord.moved(atkDirection));
        }

        this.attackerFinalPosition = atkCoord;

        return MovResponse.OK;
    }

    @Override
    public String name() { return "Gunner"; }
}
