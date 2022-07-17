package logic.attackType;

import logic.*;

import java.util.ArrayList;
import java.util.List;

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

    public abstract List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir);

    public abstract MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection);

    public abstract String name();

    public boolean attacksFriends()
    { return false; }

    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, Direction dir)
    { return attackedCoords(pieceAt, from, pieceAt.apply(from), dir); }

    public boolean canAttackFrom(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    { return !attackedCoords(pieceAt, from, piece, dir).isEmpty(); }

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
        var defCoordList = attackedCoords(game::pieceAt, atkCoord, atkDirection);
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
            var knockedCoord = defCoord.moved(atkDirection);
            if (GameState.inbounds(knockedCoord)) {
                var terrain = game.board().get(knockedCoord);
                if (defPiece.machine().type().walksOn(terrain)) {
                    game.movePiece(defCoord, knockedCoord);
                    defFinalCoord = knockedCoord;
                }
            }
        }
        return new MovResult(atkCoord, List.of(defFinalCoord), List.of(defPiece));
    }

    protected boolean knockbackOnEqualCombatPower()
    { return true; }

    public boolean walksOn(Terrain terrain)
    { return terrain != Terrain.CHASM; }

    // Common implementation for getAttackedCoords

    protected List<Coord> firstInAttackRange(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    {
        var defCoord = from.moved(dir);
        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(defCoord)) {
                break;
            }
            var defPiece = pieceAt.apply(defCoord);
            if (defPiece != null && (attacksFriends() || !defPiece.player().equals(piece.player()))) {
                return List.of(defCoord);
            }
            defCoord = defCoord.moved(dir);
        }
        return List.of();
    }

    protected List<Coord> lastInAttackRange(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    {
        var curCoord   = from.moved(dir);
        Coord defCoord = null;

        for (int i = 0; i < attackRange; i++) {
            if (!GameState.inbounds(curCoord)) {
                break;
            }
            var curPiece = pieceAt.apply(curCoord);
            if (curPiece != null && (attacksFriends() || !curPiece.player().equals(piece.player()))) {
                defCoord = curCoord;
            }
            curCoord = curCoord.moved(dir);
        }
        return defCoord == null ? List.of() : List.of(defCoord);
    }
}
