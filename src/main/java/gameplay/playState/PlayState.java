package gameplay.playState;

// :PatternUsed State

import gameplay.GameGridModel;
import gameplay.GameView;
import gameplay.IGameController;
import logic.IGameState;

import javax.swing.*;
import java.awt.event.KeyListener;

public abstract class PlayState
{
    public static PlayState initialState(GameView gameView, IGameState game, IGameController controller, GameGridModel grid, JFrame gameFrame) {
        return new SelectionPlayState(gameView, game, controller, grid, gameFrame);
    }

    protected KeyListener keyListener;

    public KeyListener getKeyListener() { return this.keyListener; }

    public abstract long getInfoPanelFlags();

    // State transition methods

    public void finishMove() {
        throw new UnsupportedOperationException("Illegal state transition");
    }

    public void startMove() {
        throw new UnsupportedOperationException("Illegal state transition");
    }
}
