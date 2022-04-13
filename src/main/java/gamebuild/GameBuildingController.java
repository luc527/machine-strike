package gamebuild;

import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class GameBuildingController
{
    private GameBuilder config = new GameBuilder();
    private List<GameSelectionObserver> selecObservers = new ArrayList<>();
    private List<PiecePlacementObserver> pieceObservers = new ArrayList<>();

    public void attach(GameSelectionObserver obs)
    {
        selecObservers.add(obs);
    }

    public void attach(PiecePlacementObserver obs)
    {
        pieceObservers.add(obs);
    }

    public void selectStartingPlayer(Player p)
    {
        config.setStartingPlayer(p);
        selecObservers.forEach(o -> o.selectedStartingPlayer(p));
    }

    public void selectBoard(Board b)
    {
        config.setBoard(b);
        selecObservers.forEach(o -> o.selectedBoard(b));
    }

    public void confirmSelections()
    {
        selecObservers.forEach(o -> o.selectionsConfirmed());
    }


}
