package logic.attackType;

import logic.*;

import java.util.List;

public class SwoopMachineType extends MachineType
{
    public SwoopMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(game, from, piece, dir); }

    @Override
    public String name() { return "Swoop"; }

    @Override
    public int combatPowerOffset(Terrain terrain)
    {
        return 1 + Math.max(0, super.combatPowerOffset(terrain));
        // Gains +1 combat power on all terrains, ignores terrains penalties
    }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var result = performBasicAttack(game, atkCoord, atkDirection);
        if (!result.success()) return result;

        var defCoord = result.defendingPieceCoords().get(0);
        var atkPiece = game.pieceAt(atkCoord);

        // Swoop always moves next to the attacked piece
        var atkFinalCoord = atkCoord;
        if (!atkPiece.dead()) {
            var nextSpace = atkFinalCoord.moved(atkDirection);
            while (GameState.inbounds(nextSpace) && !nextSpace.equals(defCoord)) {
                atkFinalCoord = nextSpace;
                nextSpace = nextSpace.moved(atkDirection);
            }
            game.movePiece(atkCoord, atkFinalCoord);
        }

        return new MovResult(atkFinalCoord, result.defendingPieceCoords(), result.defendingPieces());
    }
}
