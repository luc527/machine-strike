package gamebuild.placement;

import gamebuild.IMachineInventory;
import logic.CoordState;
import logic.Piece;
import logic.Player;

import java.util.Optional;

public interface IPiecePlacementController
{
    void attach(IPiecePlacementObserver obs);

    Player getPlacingPlayer();

    IMachineInventory getPlayerInventory(Player player);

    Optional<Piece> getPieceAt(CoordState coord);

    PlacementRequestStatus placePiece(String machine, CoordState coord);  // implicitely of placing player
}