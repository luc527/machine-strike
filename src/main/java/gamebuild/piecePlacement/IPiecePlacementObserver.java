package gamebuild.piecePlacement;

import logic.ICoord;
import logic.Piece;
import logic.Player;

import java.util.Set;

public interface IPiecePlacementObserver
{
    void pieceSelected(String machine, Player byPlayer, ICoord initialPos, Set<ICoord> availablePos);

    void selectionCancelled(Player byPlayer);

    void piecePlaced(Piece piece, ICoord pos, Player nextPlayer);

    void allPiecesPlaced();
}
