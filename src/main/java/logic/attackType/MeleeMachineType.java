package logic.attackType;

import logic.*;

import java.util.List;

public class MeleeMachineType extends MachineType
{
    public MeleeMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(pieceAt, from, piece, dir); }

    @Override
    public String name() { return "Melee"; }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        return performBasicAttack(game, atkCoord, atkDirection);
    }
}
