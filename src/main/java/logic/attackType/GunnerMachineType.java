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
    public List<Coord> getAttackedCoords(Coord from, Direction dir)
    {
        var c = from;
        for (var i = 0; i < attackRange; i++)
            c = c.moved(dir);
        return List.of(c);
    }

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
        // TODO Copy-pasted from MeleeMachineType
        //  figure out later, after implementing each MachineType, the right way to compress this

        var atkPiece = game.getPiece(atkCoord);

        // This is the only difference between this and MeleeMachineType
        var defCoord = atkCoord;
        for (var i = 0; i < attackRange; i++)
            defCoord = defCoord.moved(atkDirection);

        if (!GameState.inbounds(defCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;

        var defPiece = game.getPiece(defCoord);

        if (defPiece == null) return MovResponse.ATK_EMPTY;
        if (defPiece.player().equals(atkPiece.player())) return MovResponse.ATK_FRIEND;

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
