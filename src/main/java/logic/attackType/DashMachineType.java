package logic.attackType;

import logic.*;

import java.util.ArrayList;
import java.util.List;

public class DashMachineType extends MachineType
{
    public DashMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> getAttackedCoords(Coord from, Direction dir)
    {
        var attackedCoords = new ArrayList<Coord>(attackRange - 1);
        var coord = from;
        for (var i = 0; i < attackRange - 1; i++) {
            coord = coord.moved(dir);
            attackedCoords.add(coord);
        }
        return attackedCoords;
    }

    @Override
    public boolean attacksFriends()
    { return true; }

    @Override
    public int getAttackingPieceDamage(IGameState game, int diff)
    { return 0; }

    @Override
    public String toString() { return "DashAttack("+attackRange+")"; }

    @Override
    public String name() { return "Dash"; }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var atkPiece = game.getPiece(atkCoord);

        var landingCoord = atkCoord.moved(atkDirection, attackRange);
        if (!GameState.inbounds(landingCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;
        if (game.pieceAt(landingCoord) != null) return MovResponse.LANDS_ON_NOT_EMPTY;

        for (var defCoord : getAttackedCoords(atkCoord, atkDirection)) {
            var defPiece = game.getPiece(defCoord);
            if (defPiece == null) continue;
            defPiece.setDirection(defPiece.direction().cycle(true).cycle(true)); // Rotate 180º

            var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);
            game.dealDamage(defCoord, getDefendingPieceDamage(game, combatPowerDiff));
        }

        game.movePiece(atkCoord, landingCoord);

        this.attackerFinalPosition = landingCoord;

        return MovResponse.OK;
    }

}
