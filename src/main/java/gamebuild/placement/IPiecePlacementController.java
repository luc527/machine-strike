package gamebuild.placement;

import gamebuild.IMachineInventory;
import logic.*;

import java.util.Optional;
import java.util.Set;

public interface IPiecePlacementController
{
    void attach(IPiecePlacementObserver obs);

    Board getBoard();

    Player getPlacingPlayer();

    IMachineInventory getPlayerInventory(Player player);

    ICoord getInitialAvailablePosition();

    Set<ICoord> getAvailablePositions();

    Optional<Piece> getPieceAt(ICoord coord);

    boolean placePiece(String machine, ICoord coord);  // implicitely of placing player
}