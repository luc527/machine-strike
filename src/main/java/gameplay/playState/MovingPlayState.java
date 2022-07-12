package gameplay.playState;

import gameplay.GameGridModel;
import gameplay.IGameController;
import gameplay.InfoPanel;
import logic.IGameState;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MovingPlayState extends PlayState
{
    private final IGameState game;
    private final IGameController controller;
    private final GameGridModel grid;
    private final JFrame gameFrame;

    MovingPlayState(IGameState game, IGameController controller, GameGridModel grid, JFrame gameFrame)
    {
        this.game = game;
        this.controller = controller;
        this.grid = grid;
        this.gameFrame = gameFrame;
        this.keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                var row = grid.getCursor().row();
                var col = grid.getCursor().col();
                var dir = grid.getCarriedPieceDirection();
                if (k == KeyEvent.VK_BACK_SPACE || k == KeyEvent.VK_ESCAPE) {
                    controller.unselectPiece();
                } else if (k == KeyEvent.VK_ENTER) {
                    controller.performMovement(row, col, dir);
                } else if (c == 'k') {
                    controller.performAttack(row, col, dir);
                }else if (grid.isCarryingPiece()) {
                    if      (c == 'q') grid.rotateCarriedPiece(false);
                    else if (c == 'e') grid.rotateCarriedPiece(true);
                    gameFrame.repaint();
                }
            }
        };
    }

    public long getInfoPanelFlags()
    {
        var flags = InfoPanel.EMPTY_FLAGS;
        flags |= InfoPanel.ESC_TO_DESELECT;
        flags |= InfoPanel.ENTER_TO_PLACE;

        var coord = grid.getCursor();
        var piece = grid.getCarriedPiece();
        var dir   = grid.getCarriedPieceDirection();
        var attack = piece.machine().type();

        var isAttacking = attack.isAttacking(grid::pieceAt, coord, piece, dir);
        var isRunning   = grid.isReachable(coord.row(), coord.col()).inRunning();

        if (isAttacking && !isRunning) {
            flags |= InfoPanel.K_TO_ATTACK;
        }
        return flags;
    }

    public PlayState finishMove()
    {
        return new SelectionPlayState(game, controller, grid, gameFrame);
    }
}