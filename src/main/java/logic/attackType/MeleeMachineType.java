package logic.attackType;

import logic.*;

import java.util.List;

public class MeleeMachineType extends MachineType
{
    public MeleeMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> getAttackedCoords(Coord from, Direction dir) { return List.of(from.moved(dir)); }

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
        var atkPiece = game.getPiece(atkCoord);
        var defCoord = atkCoord.moved(atkDirection);

        if (!GameState.inbounds(defCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;

        var defPiece = game.getPiece(defCoord);

        if (defPiece == null) return MovResponse.ATK_EMPTY;
        if (defPiece.player().equals(atkPiece.player())) return MovResponse.ATK_FRIEND;

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
