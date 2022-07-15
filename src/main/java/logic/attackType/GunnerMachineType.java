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
    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    { return lastInAttackRange(pieceAt, from, piece, dir); }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        return performBasicAttack(game, atkCoord, atkDirection);
    }

    @Override
    public String name() { return "Gunner"; }
}
