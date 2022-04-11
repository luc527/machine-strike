package gameconfig;

import logic.Board;
import logic.Direction;
import logic.Machine;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class GameConfigurationController
{
    private GameConfiguration config = new GameConfiguration();
    private List<GameConfigurationObserver> observers = new ArrayList<>();

    // For when the players alternate in placing pieces in the board
    private Player currentlyPlacingPlayer;

    public void attach(GameConfigurationObserver obs) {observers.add(obs);}

    public void selectStartingPlayer(Player p)
    {
        config.setStartingPlayer(p);
        currentlyPlacingPlayer = p;
        observers.forEach(o -> {
            o.startingPlayerSetTo(p);
            o.placingPlayerSetTo(p);
        });
    }

    public void selectBoard(Board b)
    {
        config.setBoard(b);
        observers.forEach(o -> o.selectedBoard(b));
    }

    public void placePiece(Machine m, Direction d, int row, int col)
    {
        var piece = config.addPiece(currentlyPlacingPlayer, m, d, row, col);
        currentlyPlacingPlayer = currentlyPlacingPlayer.next();
        observers.forEach(o -> {
            o.piecePlaced(piece, row, col);
            o.placingPlayerSetTo(currentlyPlacingPlayer);
        });

    }

}
