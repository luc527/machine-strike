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
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            // See MeleeResponse
            return MovResponse.NO_ATTACKED_PIECE_IN_RANGE;
        }
        var defCoord = defCoordList.get(0);
        var res = performBasicAttack(game, atkCoord, atkDirection, defCoord);
        if (res != MovResponse.OK) return res;

        var atkPiece = game.pieceAt(atkCoord);

        // Swoop always moves next to the attacked piece
        if (!atkPiece.dead()) {
            var finalCoord = atkCoord;
            var nextSpace = finalCoord.moved(atkDirection);
            while (GameState.inbounds(nextSpace) && !nextSpace.equals(defCoord)) {
                finalCoord = nextSpace;
                nextSpace = nextSpace.moved(atkDirection);
            }
            game.movePiece(atkCoord, finalCoord);

            this.attackerFinalPosition = finalCoord;
        }

        return MovResponse.OK;
    }
}
