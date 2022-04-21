package gamebuild_old;

import assets.Machines;
import gamebuild.GameBuilder;
import gamebuild.MachineInventoryState;
import logic.*;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;

// TODO refactor into two controllers, one for "game selection" and another for "piece placement"

public class GameBuildingController
{
    private final GameBuilder gameBuilder = new GameBuilder();
    private final List<GameSelectionObserver> selecObservers = new ArrayList<>();
    private final List<PiecePlacementObserver> pieceObservers = new ArrayList<>();

    private Player placingPlayer;
    private final MachineInventoryState p1inventory = MachineInventoryState.initial();
    private final MachineInventoryState p2inventory = MachineInventoryState.initial();
    private CoordState boardCursor;
    private String selectedMachineName;

    private final static List<CoordState> p1availablePlacements;
    private final static List<CoordState> p2availablePlacements;

    static {
        p1availablePlacements = new ArrayList<>();
        p2availablePlacements = new ArrayList<>();
        for (var col = 0; col < Constants.BOARD_COLS; col++) {
            p1availablePlacements.add(new CoordState(Constants.BOARD_ROWS-1, col));
            p1availablePlacements.add(new CoordState(Constants.BOARD_ROWS-2, col));
            p2availablePlacements.add(new CoordState(0, col));
            p2availablePlacements.add(new CoordState(1, col));
        }
    }

    public void attach(GameSelectionObserver obs)
    {
        selecObservers.add(obs);
    }

    public void attach(PiecePlacementObserver obs)
    {
        pieceObservers.add(obs);
    }

    public void selectBoard(Board b)
    {
        gameBuilder.setBoard(b);
        selecObservers.forEach(o -> o.selectedBoard(b));
    }

    public Board getBoard()
    {
        return gameBuilder.board();
    }

    public void selectStartingPlayer(Player p)
    {
        gameBuilder.setStartingPlayer(p);
        placingPlayer = p;
        selecObservers.forEach(o -> o.selectedStartingPlayer(p));
    }

    public Player getStartingPlayer()
    {
        return gameBuilder.startingPlayer();
    }

    public void confirmSelections()
    {
        selecObservers.forEach(o -> o.selectionsConfirmed());
    }

    public Player placingPlayer()
    {
        return this.placingPlayer;
    }

    public void setMachineCursorOver(String machineName)
    {
        pieceObservers.forEach(o -> o.machineCursorOver(machineName));
    }

    public MachineInventoryState getPlayerInventory(Player p)
    {
        return p == Player.PLAYER1 ? p1inventory : p2inventory;
    }

    public boolean selectMachine(String machineName)
    {
        this.selectedMachineName = machineName;
        if (getPlayerInventory(placingPlayer).getAmount(machineName) > 0) {
            pieceObservers.forEach(o -> o.machineSelected(machineName));
            return true;
        } else {
            return false;
        }
    }

    public List<CoordState> availablePlacements()
    {
        return placingPlayer == Player.PLAYER1
             ? p1availablePlacements
             : p2availablePlacements;
    }

    public void setBoardCursor(CoordState coord)
    {
        boardCursor = coord;
        pieceObservers.forEach(o -> o.boardCursorOver(coord));
    }

    public void cancelCurrentPlacement()
    {
        boardCursor = null;
        selectedMachineName = null;
        pieceObservers.forEach(o -> o.currentPlacementCancelled());
    }

    public void confirmCurrentPlacement()
    {
        var selectedMachine = Machines.get(selectedMachineName);
        var piece = new Piece(selectedMachine, Direction.NORTH, placingPlayer);
        if (!gameBuilder.addPiece(piece, boardCursor.row(), boardCursor.col())) {
            pieceObservers.forEach(o -> o.currentPlacementFailed());
        } else {
            // TODO should also signal the end of the placement phase
            getPlayerInventory(placingPlayer).take(selectedMachineName);
            selectedMachineName = null;
            placingPlayer = placingPlayer.next();
            pieceObservers.forEach(o -> o.currentPlacementConfirmed());
        }

    }

    public Piece pieceAt(CoordState coord)
    {
        return gameBuilder.pieceAt(coord.row(), coord.col());
    }

}