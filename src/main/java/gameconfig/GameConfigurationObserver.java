package gameconfig;

import logic.Board;
import logic.Piece;
import logic.Player;

public interface GameConfigurationObserver
{
    void startingPlayerSetTo(Player p);

    void selectedBoard(Board b);

    void piecePlaced(Piece p, int row, int col);

    void placingPlayerSetTo(Player p);
}
