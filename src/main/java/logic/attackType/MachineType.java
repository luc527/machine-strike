package logic.attackType;

import logic.*;

import java.util.ArrayList;
import java.util.List;

// TODO fix! regarding performBasicAttack, the types using this function
//  can't know whether a knockback happened or not, so this is even more
//  reason for, at least internally, encapsulate MovResponse withint a MovResult
//  or something like that that says the resulting positions of each piece

public abstract class MachineType
{
    protected int attackRange;

    public MachineType(int attackRange)
    {
        this.attackRange = attackRange;
    }

    public List<Coord> coordsInAttackRange(Coord from, Direction dir)
    {
        var list = new ArrayList<Coord>(attackRange);
        var curr = from.moved(dir);
        for (var i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(curr)) break;
            list.add(curr);
            curr = curr.moved(dir);
        }
        return list;
    }

    // We have two modes of checking for attacked coords:
    // One where you pass the piece, and one where you don't.
    // The first is to be used when visualizing the attack, when you haven't performed the preceding move yet.
    // The other one is implemented in terms of the first one, with the piece being the one at the given coord.

    public abstract List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir);

    public abstract MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection);

    public abstract String name();

    public boolean attacksFriends()
    { return false; }

    public List<Coord> attackedCoords(IGameState game, Coord from, Direction dir)
    { return attackedCoords(game, from, game.pieceAt(from), dir); }

    public boolean canAttackFrom(IGameState game, Coord from, IPiece piece, Direction dir)
    { return !attackedCoords(game, from, piece, dir).isEmpty(); }

    public int combatPowerOffset(Terrain terrain)
    { return terrain.combatPowerOffset(); }

    public int getAttackingPieceDamage(IGameState game, int combatPowerDiff)
    {
        return combatPowerDiff == 0 && knockbackOnEqualCombatPower()
             ? 1
             : game.getAttackingPieceDamage(combatPowerDiff);
    }

    public int getDefendingPieceDamage(IGameState game, int combatPowerDiff)
    {
        return combatPowerDiff == 0 && knockbackOnEqualCombatPower()
             ? 1
             : game.getDefendingPieceDamage(combatPowerDiff);
    }

    protected MovResult performBasicAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            return new MovResult(MovResponse.NO_ATTACKED_PIECE_IN_RANGE);
        }
        var defCoord = defCoordList.get(0);

        var defPiece = game.getPiece(defCoord);
        var atkPiece = game.getPiece(atkCoord);

        assert defPiece != null;
        assert atkPiece != null;

        var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);

        game.dealDamage(atkCoord, getAttackingPieceDamage(game, combatPowerDiff));
        game.dealDamage(defCoord, getDefendingPieceDamage(game, combatPowerDiff));

        var defFinalCoord = defCoord;
        if (combatPowerDiff == 0 && knockbackOnEqualCombatPower() && !defPiece.dead()) {
            defFinalCoord = defCoord.moved(atkDirection);
            game.movePiece(defCoord, defFinalCoord);
        }
        return new MovResult(atkCoord, List.of(defFinalCoord), List.of(defPiece));
    }

    protected boolean knockbackOnEqualCombatPower()
    { return true; }

    // Common implementation for getAttackedCoords

    protected List<Coord> firstInAttackRange(IGameState game, Coord from, IPiece piece, Direction dir)
    {
        var defCoord = from.moved(dir);
        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(defCoord)) {
                break;
            }
            var defPiece = game.pieceAt(defCoord);
            if (defPiece != null && (attacksFriends() || !defPiece.player().equals(piece.player()))) {
                return List.of(defCoord);
            }
            defCoord = defCoord.moved(dir);
        }
        return List.of();
    }

    protected List<Coord> lastInAttackRange(IGameState game, Coord from, IPiece piece, Direction dir)
    {
        var curCoord   = from.moved(dir);
        Coord defCoord = null;

        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(curCoord)) {
                break;
            }
            var curPiece = game.pieceAt(curCoord);
            if (curPiece != null && (attacksFriends() || !curPiece.player().equals(piece.player()))) {
                defCoord = curCoord;
            }
            curCoord = curCoord.moved(dir);
        }
        return defCoord == null ? List.of() : List.of(defCoord);
    }

}
