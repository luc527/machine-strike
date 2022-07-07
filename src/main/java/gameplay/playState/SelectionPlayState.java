package gameplay.playState;

import gameplay.GameGridModel;
import gameplay.IGameController;
import gameplay.InfoPanel;
import logic.IGameState;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SelectionPlayState extends PlayState
{
    private final IGameState game;
    private final IGameController controller;
    private final GameGridModel grid;
    private final JFrame gameFrame;

    public SelectionPlayState(IGameState game, IGameController controller, GameGridModel grid, JFrame gameFrame)
    {
        this.game = game;
        this.controller = controller;
        this.grid = grid;
        this.gameFrame = gameFrame;
        this.keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var k = e.getKeyCode();
                var c = e.getKeyChar();
                if (k == KeyEvent.VK_ENTER && !game.playerRanOutOfMoves()) {
                    var cursor = grid.getCursor();
                    if (!controller.selectPiece(cursor.row(), cursor.col())) return;
                    grid.carryPieceFrom(cursor);
                } else if (c == 'f') {
                    controller.finishTurn();
                }
            }
        };
    }

    @Override
    public long getInfoPanelFlags()
    {
        var flags = InfoPanel.EMPTY_FLAGS;
        flags |= InfoPanel.ENTER_TO_SELECT;
        if (game.currentPlayerMoves() > 0) flags |= InfoPanel.F_TO_FINISH_TURN;
        return flags;
    }

    @Override
    public PlayState startMove()
    {
        return new MovingPlayState(game, controller, grid, gameFrame);
    }
}
