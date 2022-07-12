package logic.attackType;

import logic.*;

import java.util.List;

public class RamMachineType extends MachineType
{
    public RamMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> getAttackedCoords(Coord from, Direction dir)
    { return List.of(from.moved(dir)); }

    @Override
    public boolean attacksFriends()
    { return false; }

    @Override
    public String toString()
    { return "MeeleeAttack("+attackRange+")"; }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        // Copy-pasted from MeleeMachineType

        var atkPiece = game.getPiece(atkCoord);
        var defCoord = atkCoord.moved(atkDirection);

        if (!GameState.inbounds(defCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;

        var defPiece = game.getPiece(defCoord);

        if (defPiece == null) return MovResponse.ATK_EMPTY;
        if (defPiece.player().equals(atkPiece.player())) return MovResponse.ATK_FRIEND;

        var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);
        game.dealDamage(defCoord, getAttackingPieceDamage(game, combatPowerDiff));
        game.dealDamage(atkCoord, getDefendingPieceDamage(game, combatPowerDiff));

        // This is where the Ram attack type differs: there's always knockback
        // and the attacking piece moves onto the square left by the defending piece

        var defKnocked = false;
        if (!defPiece.dead()) {
            var knockCoord = defCoord.moved(atkDirection);
            if (GameState.inbounds(knockCoord)) {
                game.movePiece(defCoord, knockCoord);
                defKnocked = true;
            }
        }

        attackerFinalPosition = atkCoord;
        if (!atkPiece.dead() && defKnocked) {
            game.movePiece(atkCoord, defCoord);
            attackerFinalPosition = defCoord;
        }

        return MovResponse.OK;
    }

    @Override
    public String name() { return "Ram"; } }
