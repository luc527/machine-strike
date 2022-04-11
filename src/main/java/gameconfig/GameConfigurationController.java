package gameconfig;

import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class GameConfigurationController
{
    private GameConfiguration config = new GameConfiguration();
    private List<GameConfigurationObserver> observers = new ArrayList<>();

    public void attach(GameConfigurationObserver obs) {observers.add(obs);}

    public void selectStartingPlayer(Player p)
    {
        config.setStartingPlayer(p);
        observers.forEach(o -> o.selectedStartingPlayer(p));
    }

    public void selectBoard(Board b)
    {
        config.setBoard(b);
        observers.forEach(o -> o.selectedBoard(b));
    }

    public void confirmStartingPlayerAndBoard()
    {
        observers.forEach(o -> o.startingPlayerAndBoardConfirmed());
    }


}
