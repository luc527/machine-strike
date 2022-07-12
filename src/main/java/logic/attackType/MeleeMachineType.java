package logic.attackType;

import logic.*;

import java.util.List;

public class MeleeMachineType extends MachineType
{
    public MeleeMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, Direction dir)
    { return firstInAttackRange(game, from, dir); }

    @Override
    public boolean attacksFriends() { return false; }

    @Override
    public String name() { return "Melee"; }

    @Override
    public int getAttackingPieceDamage(IGameState game, int diff)
    { return diff == 0 ? 1 : super.getAttackingPieceDamage(game, diff); }

    @Override
    public int getDefendingPieceDamage(IGameState game, int diff)
    { return diff == 0 ? 1 : super.getDefendingPieceDamage(game, diff); }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            // Actually here we can't know whether the attacked coord is empty, or has a friend, or is out of bounds!
            // And now that we consider potentially more than one coord (firstInAttackRange), which one should we consider?
            // Maybe now we don't have the luxury of all those specialized error codes
            // We could generalize those errors to something like NO_ATTACKED_PIECE_IN_RANGE
            return MovResponse.ATK_EMPTY;
        }
        // But at this point we do have the guarantee that defCoord is in bounds and has an enemy piece
        var defCoord = defCoordList.get(0);
        var defPiece = game.getPiece(defCoord);
        var atkPiece = game.getPiece(atkCoord);

        var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);

        game.dealDamage(atkCoord, getAttackingPieceDamage(game, combatPowerDiff));
        game.dealDamage(defCoord, getDefendingPieceDamage(game, combatPowerDiff));

        // Knockback
        if (combatPowerDiff == 0 && !defPiece.dead()) {
            game.movePiece(defCoord, defCoord.moved(atkDirection));
        }

        this.attackerFinalPosition = atkCoord;

        return MovResponse.OK;
    }

    @Override
    public String toString() { return "MeleeAttack("+attackRange+")"; }
}
