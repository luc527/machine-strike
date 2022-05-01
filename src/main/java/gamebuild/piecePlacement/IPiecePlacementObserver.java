package gamebuild.piecePlacement;

import logic.Coord;
import logic.Machine;
import logic.Piece;
import logic.Player;

import java.util.Set;

public interface IPiecePlacementObserver
{
    void show(Player firstPlayer);

    void pieceSelected(Machine machine, Player byPlayer, Coord initialPos, Set<Coord> availablePos);

    void selectionCancelled(Player byPlayer);

    void piecePlaced(Piece piece, Coord pos, Player nextPlayer);

    void allPiecesPlaced();
}
