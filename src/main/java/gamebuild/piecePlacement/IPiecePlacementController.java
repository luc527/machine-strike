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

// TODO!!! enable players to rotate the pieces

public interface IPiecePlacementController
{
    void attach(IPiecePlacementObserver obs);

    Player getFirstPlayer();

    Board getBoard();

    IMachineInventory getPlayerInventory(Player player);

    Optional<Piece> getPieceAt(Coord coord);

    boolean selectMachine(String machine);

    void cancelSelection();

    boolean placeMachine(Coord coord, String machine, Direction direction);

    void startGame();
}