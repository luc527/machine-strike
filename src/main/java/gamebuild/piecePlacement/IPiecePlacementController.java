package gamebuild.piecePlacement;

import gamebuild.IMachineInventory;
import logic.*;

import java.awt.event.ActionEvent;
import java.util.Optional;


// Observe how the player is never passed
// This is because the controller is responsible for keeping
// track of the current placing player, and ensuring that
// the players alternate correctly. Otherwise two sequential calls
// to placePiece(Player p, ...) could have the same player,
// which would be wrong.

public interface IPiecePlacementController
{
    void startPlacement();

    void attach(IPiecePlacementObserver obs);

    Board getBoard();

    IMachineInventory getPlayerInventory(Player player);

    Piece getPieceAt(Coord coord);

    boolean selectMachine(Machine machine);

    void cancelSelection();

    boolean placeMachine(Coord coord, Machine machine, Direction direction);

    void startGame();
}
