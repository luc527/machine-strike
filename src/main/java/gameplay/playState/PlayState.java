package gameplay.playState;

// :PatternUsed State

import gameplay.GameGridModel;
import gameplay.IGameController;
import logic.IGameState;

import javax.swing.*;
import java.awt.event.KeyListener;

public abstract class PlayState
{
    public static PlayState initialState(IGameState game, IGameController controller, GameGridModel grid, JFrame gameFrame) {
        return new SelectionPlayState(game, controller, grid, gameFrame);
    }

    protected KeyListener keyListener;

    public KeyListener getKeyListener() { return this.keyListener; }

    public abstract long getInfoPanelFlags();

    // State transition methods

    public PlayState finishMove() {
        throw new UnsupportedOperationException("Illegal state transition");
    }

    public PlayState startMove() {
        throw new UnsupportedOperationException("Illegal state transition");
    }
}
