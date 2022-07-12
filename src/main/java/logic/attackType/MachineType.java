package logic.attackType;

import logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



// TODO!!!! CONSIDER REACHABILITY IN ATTACKEDCOORDS
// Maybe it'd be more orthogonal to implement a .filterAttackedCoords(List<Coord> coordsInAttackRange)
// that each MachineType implements
// that will filter for the first/last in attack range, coords containing enemy pieces and so on



public abstract class MachineType
{
    protected int attackRange;
    protected Coord attackerFinalPosition;

    public MachineType(int attackRange)
    {
        this.attackRange = attackRange;
    }

    public List<Coord> coordsInAttackRange(IGameState game, Coord from, Direction dir)
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

    public abstract List<Coord> attackedCoords(IGameState game, Coord from, Direction dir);

    public abstract boolean attacksFriends();

    public abstract MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection);

    public abstract String name();

    public int combatPowerOffset(Terrain terrain)
    { return terrain.combatPowerOffset(); }

    public int getAttackingPieceDamage(IGameState game, int combatPowerDiff)
    { return game.getAttackingPieceDamage(combatPowerDiff); }

    public int getDefendingPieceDamage(IGameState game, int combatPowerDiff)
    { return game.getDefendingPieceDamage(combatPowerDiff); }

    public Coord attackerFinalPosition()
    { return this.attackerFinalPosition; }

    // Common implementation for getAttackedCoords
    // ! TODO These could be implemented as strategies
    protected List<Coord> firstInAttackRange(IGameState game, Coord from, Direction dir)
    {
        var atkPiece = game.pieceAt(from);
        var defCoord = from.moved(dir);
        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(defCoord)) {
                break;
            }
            var defPiece = game.pieceAt(defCoord);
            if (defPiece != null && !defPiece.player().equals(atkPiece.player())) {
                return List.of(defCoord);
            }
            defCoord = defCoord.moved(dir);
        }
        return List.of();
    }

    protected List<Coord> lastInAttackRange(IGameState game, Coord from, Direction dir)
    {
        var atkPiece   = game.pieceAt(from);
        var curCoord   = from.moved(dir);
        Coord defCoord = null;

        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(curCoord)) {
                break;
            }
            var curPiece = game.pieceAt(curCoord);
            if (curPiece != null && !curPiece.player().equals(atkPiece.player())) {
                defCoord = curCoord;
            }
            curCoord = curCoord.moved(dir);
        }
        return defCoord == null ? List.of() : List.of(defCoord);
    }
}
