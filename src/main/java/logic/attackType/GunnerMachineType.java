package logic.attackType;

import logic.*;

import java.util.List;

public class GunnerMachineType extends MachineType
{
    public GunnerMachineType(int attackRange)
    {
        super(attackRange);
    }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return lastInAttackRange(game, from, piece, dir); }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        // This implementation is the same as in MeleeMachineType

        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            // See MeleeMachineType
            return MovResponse.NO_ATTACKED_PIECE_IN_RANGE;
        }
        var defCoord = defCoordList.get(0);

        return performBasicAttack(game, atkCoord, atkDirection, defCoord);
    }

    @Override
    public String name() { return "Gunner"; }
}
