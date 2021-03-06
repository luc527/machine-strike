package gamebuild.piecePlacement;

import components.boardgrid.BoardGridModel;
import logic.*;

import java.util.Objects;
import java.util.Set;

public class PlacementGridModel extends BoardGridModel
{
    public static final PieceProvider nullPieceProvider = c -> null;

    private PieceProvider pieceProvider;
    private Machine carriedMachine;
    private Direction carriedMachineDirection;

    private Set<Coord> availablePositions = Set.of();

    public IPiece pieceAt(int row, int col)
    { return pieceProvider.apply(Coord.create(row, col)); }

    public PlacementGridModel(Board board, PieceProvider pieceProvider)
    {
        super(board);
        setPieceProvider(pieceProvider);
    }

    public void setPieceProvider(PieceProvider pieceProvider)
    { this.pieceProvider = Objects.requireNonNullElse(pieceProvider, nullPieceProvider); }

    public void carryMachine(Machine m)
    {
        carriedMachine = m;
        carriedMachineDirection = Direction.NORTH;
    }

    public void rotateCarriedMachine(boolean right)
    {
        if (carriedMachineDirection == null)
            throw new RuntimeException("No carried machine to rotate (carriedMachineDirection null)");
        carriedMachineDirection = carriedMachineDirection.cycle(right);
    }

    public boolean isCarryingMachine()
    { return carriedMachine != null; }

    public Machine getCarriedMachine()
    { return carriedMachine; }

    public Direction getCarriedMachineDirection()
    { return carriedMachineDirection; }

    public void setAvailablePositions(Set<Coord> availablePositions)
    { this.availablePositions = availablePositions; }

    @Override
    public boolean isAvailable(int row, int col)
    { return availablePositions.contains(Coord.create(row, col)); }
}
