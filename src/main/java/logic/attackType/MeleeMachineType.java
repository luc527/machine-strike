package logic.attackType;

import logic.*;

import java.util.List;

public class MeleeMachineType extends MachineType
{
    public MeleeMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> getAttackedCoords(Coord from, Direction dir)
    { return List.of(from.moved(dir)); }

    @Override
    public boolean attacksFriends()
    { return false; }

    @Override
    public String toString()
    { return "MeleeAttack("+attackRange+")"; }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var atkPiece = game.getPiece(atkCoord);
        var defCoord = atkCoord.moved(atkDirection);

        if (!GameLogic.inbounds(defCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;

        var defPiece = game.getPiece(defCoord);

        if (defPiece == null) return MovResponse.ATK_EMPTY;
        if (defPiece.player().equals(atkPiece.player())) return MovResponse.ATK_FRIEND;

        var conflict = game.getConflictDamages(atkCoord, defCoord, atkPiece, defPiece, atkDirection);

        game.dealDamage(defCoord, conflict.defDamage());

        if (!defPiece.dead() && conflict.knockback()) {
            game.movePiece(defCoord, defCoord.moved(atkDirection));
        }

        game.dealDamage(atkCoord, conflict.atkDamage());

        this.attackerFinalPosition = atkCoord;

        return MovResponse.OK;
    }

    @Override
    public String name() { return "Melee"; }
}
