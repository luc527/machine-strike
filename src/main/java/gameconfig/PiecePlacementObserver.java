package gameconfig;

import logic.Piece;
import logic.Player;

public interface PiecePlacementObserver
{
    void placingPlayerChangedTo(Player p);

    void pieceSelectionCursorMoved(int index);

    void pieceUnderCursorSelected();

    void tileSelectionCursorMoved(int row, int col);

    void tileUnderCursorSelected();

    // TODO
    //  both cursor can coexist, since the player can also undo a piece placement
    //  therefore I think the model also has to encode available pieces?

    // TODO ends when the sum of victory points of pieces is 10 I think
    void allPiecesPlaced();


}
