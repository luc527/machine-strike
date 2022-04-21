package gamebuild.placement;

import gamebuild.IMachineInventory;
import logic.*;

import java.util.Optional;


// Observe how the player is never passed
// This is because the controller is responsible for keeping
// track of the current placing player, and ensuring that
// the players alternate correctly. Otherwise two sequential calls
// to placePiece(Player p, ...) could have the same player,
// which would be wrong.

public interface IPiecePlacementController
{
    void attach(IPiecePlacementObserver obs);

    Player getFirstPlayer();

    boolean selectMachine(String machine);

    void cancelSelection();

    boolean placePiece(String machine, ICoord coord);

    Board getBoard();

    IMachineInventory getPlayerInventory(Player player);

    Optional<Piece> getPieceAt(ICoord coord);
}