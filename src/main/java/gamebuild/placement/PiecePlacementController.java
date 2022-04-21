package gamebuild.placement;

import assets.Machines;
import gamebuild.IMachineInventory;
import gamebuild.MachineInventoryState;
import gamebuild.GameBuilder;
import logic.*;
import utils.Constants;

import java.util.*;

public class PiecePlacementController implements IPiecePlacementController
{
    private final List<IPiecePlacementObserver> os;
    private Player placingPlayer;
    private final MachineInventoryState p1inventory;
    private final MachineInventoryState p2inventory;
    private final GameBuilder builder;

    private static final Set<ICoord> p1availablePositions;
    private static final Set<ICoord> p2availablePositions;
    private static final ICoord p1initialPosition;
    private static final ICoord p2initialPosition;

    static {
        var rows = Constants.BOARD_ROWS;
        var cols = Constants.BOARD_COLS;
        p1availablePositions = new HashSet<>();
        p2availablePositions = new HashSet<>();
        for (var col = 0; col < cols; col++) {
            p1availablePositions.add(CoordCache.get(rows-2, col));
            p1availablePositions.add(CoordCache.get(rows-1, col));
            p2availablePositions.add(CoordCache.get(0,      col));
            p2availablePositions.add(CoordCache.get(1,      col));
        }
        p1initialPosition = CoordCache.get(rows-1, cols/2);
        p2initialPosition = CoordCache.get(0,      cols/2);
    }

    public PiecePlacementController(GameBuilder builder)
    {
        this.builder = builder;
        placingPlayer = builder.startingPlayer();
        os = new ArrayList<>();
        p1inventory = MachineInventoryState.initial();
        p2inventory = MachineInventoryState.initial();
    }

    @Override
    public void attach(IPiecePlacementObserver obs)
    {
        os.add(obs);
    }

    @Override
    public Board getBoard()
    {
        return builder.board();
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
    public ICoord getInitialAvailablePosition()
    {
        return placingPlayer == Player.PLAYER1 ? p1initialPosition : p2initialPosition;
    }

    @Override
    public Set<ICoord> getAvailablePositions()
    {
        return placingPlayer == Player.PLAYER1 ? p1availablePositions : p2availablePositions;
    }

    @Override
    public Optional<Piece> getPieceAt(ICoord coord)
    {
        return Optional.ofNullable(builder.pieceAt(coord.row(), coord.col()));
    }

    @Override
    public boolean placePiece(String machine, ICoord coord)
    {
        var inv = placingPlayer == Player.PLAYER1 ? p1inventory : p2inventory;
        if (inv.getAmount(machine) <= 0) {
            throw new RuntimeException("Got to placePiece(machine, coord) without the placing player actually having the machine available!");
        }
        if (builder.pieceAt(coord.row(), coord.col()) != null) {
            return false;
        }
        inv.take(machine);
        var piece = new Piece(Machines.get(machine), Direction.NORTH, placingPlayer);
        builder.addPiece(piece, coord.row(), coord.col());
        placingPlayer = placingPlayer.next();
        return true;
    }
}
