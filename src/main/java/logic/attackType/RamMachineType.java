package logic.attackType;

import logic.*;

import java.util.List;

public class RamMachineType extends MachineType
{
    public RamMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(game, from, piece, dir); }

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
            defFinalCoord = defCoord.moved(atkDirection);
            if (GameState.inbounds(defFinalCoord)) {
                game.movePiece(defCoord, defFinalCoord);
                defKnocked = true;
            }
        }

        var atkPiece = game.pieceAt(atkCoord);
        var atkFinalCoord = atkCoord;
        if (!atkPiece.dead() && defKnocked) {
            game.movePiece(atkCoord, defCoord);
            atkFinalCoord = defCoord;
        }

        return new MovResult(atkFinalCoord, List.of(defFinalCoord), result.defendingPieces());
    }

    @Override
    public String name() { return "Ram"; } }
