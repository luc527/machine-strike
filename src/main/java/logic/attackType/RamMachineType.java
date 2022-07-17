package logic.attackType;

import logic.*;

import java.util.List;

public class RamMachineType extends MachineType
{
    public RamMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(pieceAt, from, piece, dir); }

    @Override
    public String toString()
    { return "MeeleeAttack("+attackRange+")"; }

    @Override
    public boolean knockbackOnEqualCombatPower()
    { return false; }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var result = performBasicAttack(game, atkCoord, atkDirection);
        if (!result.success()) return result;

        var defCoord = result.defendingPieceCoords().get(0);
        var defPiece = result.defendingPieces().get(0);

        var defKnocked = false;
        var defFinalCoord = defCoord;
        if (!defPiece.dead()) {
            var knockedCoord = defCoord.moved(atkDirection);
            defKnocked = GameState.inbounds(knockedCoord)
                      && game.pieceAt(knockedCoord) == null
                      && defPiece.machine().type().walksOn(game.board().get(knockedCoord));
            if (defKnocked) {
                game.movePiece(defCoord, knockedCoord);
                defFinalCoord = knockedCoord;
            }
        }

        var atkPiece = game.pieceAt(atkCoord);
        var atkFinalCoord = atkCoord;
        if (!atkPiece.dead() && defKnocked && walksOn(game.board().get(defCoord))) {
            game.movePiece(atkCoord, defCoord);
            atkFinalCoord = defCoord;
        }

        return new MovResult(atkFinalCoord, List.of(defFinalCoord), result.defendingPieces());
    }

    @Override
    public String name() { return "Ram"; } }
