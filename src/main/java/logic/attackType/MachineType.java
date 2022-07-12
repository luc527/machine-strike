package logic.attackType;

import logic.*;

import java.util.List;
import java.util.Optional;

// :PatternUsed Visitor

// TODO some things will be equal for all attack types:
//  some pieces may die which will result in victory points obtained
//  so there should be a method for retrieving those informations
//  since perform already returns MovResponse, maybe something like
//  getLastAttackResult, idk
//  although this introduces state in something that was otherwise "pure" (except for modifying the pieces)
//  or, rather, that had *no internal state*

public abstract class MachineType
{
    protected int attackRange;
    protected Coord attackerFinalPosition;

    public MachineType(int attackRange)
    {
        this.attackRange = attackRange;
    }

    public abstract List<Coord> getAttackedCoords(Coord from, Direction dir);

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

    public void accept(AttackVisitor visitor, PieceProvider pieceAt, Coord atkCoord, IPiece atkPiece, Direction dir)
    {
        if (atkPiece == null) return;

        var defCoords = getAttackedCoords(atkCoord, dir);
        for (var defCoord : defCoords) {
            var inbounds = GameState.inbounds(defCoord);
            if (!inbounds) continue;

            var defPiece = pieceAt.apply(defCoord);

            Optional<IPiece> optPiece = Optional.empty();
            if (defPiece != null) {
                var enemy = !defPiece.player().equals(atkPiece.player());
                if (enemy || attacksFriends()) optPiece = Optional.of(defPiece);
            }

            visitor.visitCoordsInAttackRange(defCoord, optPiece);
        }
    }

    public boolean isAttacking(PieceProvider pieceAt, Coord atkCoord, IPiece atkPiece, Direction dir)
    {
        var visitor = new AttackVisitor() {
            public boolean attacked = false;
            public void visitCoordsInAttackRange(Coord coord, Optional<IPiece> piece) {
                attacked = attacked || piece.isPresent();
            }
        };

        accept(visitor, pieceAt, atkCoord, atkPiece, dir);

        return visitor.attacked;
    }
}
