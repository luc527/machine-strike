package gamebuild.placement;

import assets.Machines;
import gamebuild.IMachineInventory;
import gamebuild.MachineInventoryState;
import gamebuild.GameBuilder;
import logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PiecePlacementController implements IPiecePlacementController
{
    private final List<IPiecePlacementObserver> os;
    private Player placingPlayer;
    private final MachineInventoryState p1inventory;
    private final MachineInventoryState p2inventory;
    private final GameBuilder builder;

    public PiecePlacementController(GameBuilder builder)
    {
        this.builder = builder;
        os = new ArrayList<>();
        p1inventory = MachineInventoryState.initial();
        p2inventory = MachineInventoryState.initial();
        placingPlayer = builder.startingPlayer();
    }

    @Override
    public void attach(IPiecePlacementObserver obs)
    {
        os.add(obs);
    }

    @Override
    public Player getPlacingPlayer()
    {
        return placingPlayer;
    }

    @Override
    public IMachineInventory getPlayerInventory(Player player)
    {
        return player == Player.PLAYER1 ? p1inventory : p2inventory;
    }

    @Override
    public Optional<Piece> getPieceAt(CoordState coord)
    {
        return Optional.ofNullable(builder.pieceAt(coord.row(), coord.col()));
    }

    @Override
    public PlacementRequestStatus placePiece(String machine, CoordState coord)
    {
        if (builder.pieceAt(coord.row(), coord.col()) != null) {
            return PlacementRequestStatus.OCCUPIED;
        }
        var inv = placingPlayer == Player.PLAYER1 ? p1inventory : p2inventory;
        if (!inv.take(machine)) {
            return PlacementRequestStatus.DEPLETED;
        }
        var piece = new Piece(Machines.get(machine), Direction.NORTH, placingPlayer);
        builder.addPiece(piece, coord.row(), coord.col());
        return PlacementRequestStatus.PLACED;
    }
}
