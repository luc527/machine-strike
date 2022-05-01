package gamebuild.piecePlacement;

import gamebuild.IMachineInventory;
import gamebuild.MachineInventory;
import gamebuild.GameBuilder;
import logic.*;
import constants.Constants;

import java.util.*;

public class PiecePlacementController implements IPiecePlacementController
{
    private final List<IPiecePlacementObserver> observers;
    private Player placingPlayer;
    private final MachineInventory p1inventory;
    private final MachineInventory p2inventory;
    private final GameBuilder gameBuilder;

    private static final Set<Coord> p1availablePositions;
    private static final Set<Coord> p2availablePositions;
    private static final Coord p1initialPosition;
    private static final Coord p2initialPosition;

    static {
        var rows = Constants.BOARD_ROWS;
        var cols = Constants.BOARD_COLS;
        p1availablePositions = new HashSet<>();
        p2availablePositions = new HashSet<>();
        for (var col = 0; col < cols; col++) {
            p1availablePositions.add(Coord.create(rows-2, col));
            p1availablePositions.add(Coord.create(rows-1, col));
            p2availablePositions.add(Coord.create(0,      col));
            p2availablePositions.add(Coord.create(1,      col));
        }
        p1initialPosition = Coord.create(rows-1, cols/2);
        p2initialPosition = Coord.create(0,      cols/2);
    }

    public PiecePlacementController(GameBuilder gameBuilder, MachineInventory p1inventory, MachineInventory p2inventory)
    {
        this.gameBuilder = gameBuilder;
        placingPlayer = gameBuilder.startingPlayer();
        observers = new ArrayList<>();
        this.p1inventory = p1inventory;
        this.p2inventory = p2inventory;
    }

    @Override
    public void attach(IPiecePlacementObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public Player getFirstPlayer()
    {
        return gameBuilder.startingPlayer();
    }

    @Override
    public boolean selectMachine(Machine machine)
    {
        var inv = placingPlayer == Player.PLAYER1 ? p1inventory : p2inventory;
        if (inv.getAmount(machine) <= 0) {
            return false;
        }
        var initialPos = placingPlayer == Player.PLAYER1 ? p1initialPosition : p2initialPosition;
        var availablePos = placingPlayer == Player.PLAYER1 ? p1availablePositions : p2availablePositions;
        observers.forEach(o -> o.pieceSelected(machine, placingPlayer, initialPos, availablePos));
        return true;
    }

    @Override
    public void cancelSelection()
    {
        observers.forEach(o -> o.selectionCancelled(placingPlayer));
    }

    @Override
    public Board getBoard()
    {
        return gameBuilder.board();
    }

    @Override
    public IMachineInventory getPlayerInventory(Player player)
    {
        return player == Player.PLAYER1 ? p1inventory : p2inventory;
    }

    @Override
    public Optional<Piece> getPieceAt(Coord coord)
    {
        return Optional.ofNullable(gameBuilder.pieceAt(coord.row(), coord.col()));
    }

    @Override
    public boolean placeMachine(Coord coord, Machine machine, Direction direction)
    {
        var inv = placingPlayer == Player.PLAYER1 ? p1inventory : p2inventory;
        if (inv.getAmount(machine) <= 0) {
            throw new RuntimeException("Got to placePiece(machine, coord) without the placing player actually having the machine available!");
        }
        if (gameBuilder.pieceAt(coord.row(), coord.col()) != null) {
            return false;
        }
        inv.take(machine);
        var piece = new Piece(machine, direction, placingPlayer);
        gameBuilder.addPiece(piece, coord.row(), coord.col());

        var nextInv = inv == p1inventory ? p2inventory : p1inventory;
        if (!nextInv.isEmpty()) {
            placingPlayer = placingPlayer.next();
        }
        observers.forEach(o -> o.piecePlaced(piece, coord, placingPlayer));

        if (nextInv.isEmpty() && inv.isEmpty()) {
            observers.forEach(IPiecePlacementObserver::allPiecesPlaced);
        }

        return true;
    }

    @Override
    public void startGame()
    {
        System.out.println("TODO start game");
    }
}
