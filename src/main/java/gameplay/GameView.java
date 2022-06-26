package gameplay;

import components.Palette;
import logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Function;

public class GameView implements GameObserver
{
    private final IGameController controller;

    private final JFrame frame;
    private final GameGridModel gridModel;
    private final GameGridPanel gridPanel;
    private final InfoPanel infoPanel;

    private KeyListener currentKeyListener;

    private boolean currentPlayerMoved = false;
    private Coord p1prevCoord = Coord.create(0, 0);
    private Coord p2prevCoord = Coord.create(0, 0);

    private final KeyListener selectionListener;
    private final KeyListener placementListener;  // TODO bad name, find a better one

    public GameView(IGameController controller)
    {
        this.controller = controller;
        controller.attach(this);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);

        selectionListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var k = e.getKeyCode();
                var c = e.getKeyChar();
                if (k == KeyEvent.VK_ENTER) {
                    var cursor = gridModel.getCursor();
                    if (!controller.selectPiece(cursor.row(), cursor.col())) return;
                    gridModel.carryPieceFrom(cursor);
                } else if (c == 'f') {
                    controller.finishTurn();
                }
            }
        };

        placementListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                var row = gridModel.getCursor().row();
                var col = gridModel.getCursor().col();
                var dir = gridModel.getCarriedPieceDirection();
                if (k == KeyEvent.VK_BACK_SPACE || k == KeyEvent.VK_ESCAPE) {
                    controller.unselectPiece();
                } else if (k == KeyEvent.VK_ENTER) {
                    controller.performMovement(row, col, dir, false);
                } else if (c == 'k') {
                    controller.performMovement(row, col, dir, true);
                } else if (gridModel.isCarryingPiece()) {
                    if      (c == 'q') gridModel.rotateCarriedPiece(false);
                    else if (c == 'e') gridModel.rotateCarriedPiece(true);
                    frame.repaint();
                }
            }
        };

        gridModel = new GameGridModel(controller.getBoard(), controller::pieceAt);
        gridPanel = new GameGridPanel(gridModel);

        infoPanel = new InfoPanel();
        infoPanel.display(InfoPanel.ENTER_TO_SELECT);
        panel.add(infoPanel, BorderLayout.LINE_END);

        gridModel.onMove(this::updateInfo);

        panel.add(gridPanel, BorderLayout.CENTER);
    }

    private void updateInfo()
    {
        var flags = InfoPanel.EMPTY_FLAGS;
        if (currentKeyListener == selectionListener) {
            flags |= InfoPanel.ENTER_TO_SELECT;
            if (currentPlayerMoved) flags |= InfoPanel.F_TO_FINISH_TURN;
        } else if (currentKeyListener == placementListener) {
            flags |= InfoPanel.ESC_TO_DESELECT;
            flags |= InfoPanel.ENTER_TO_PLACE;
            if (gridModel.isCarriedPieceAttacking()) flags |= InfoPanel.K_TO_ATTACK;
        }
        infoPanel.display(flags);
    }

    private void setKeyListener(KeyListener to)
    {
        gridPanel.removeKeyListener(currentKeyListener);
        gridPanel.addKeyListener(to);
        currentKeyListener = to;
        updateInfo();
    }

    @Override
    public void start(Player firstPlayer)
    {
        currentPlayerMoved = false;
        frame.pack();
        frame.setVisible(true);
        gridPanel.setCursorColor(Palette.color(firstPlayer));
        gridPanel.setFocused(true);
        setKeyListener(selectionListener);
    }

    @Override
    public void pieceSelected(int row, int col, IPiece piece, Function<Coord, Reachability> reachabilityFunction)
    {
        // TODO refactor grid model so that available positions is an abstract method that each subclass just overrides,
        //  so we don't need to do pass a set or pass a function to it
        //  so this grid model here for instance can just can GameLogic.reachability itself, which is a lot simpler
        gridModel.carryPieceFrom(Coord.create(row, col));
        gridModel.setReachabilityFunction(reachabilityFunction);
        frame.repaint();
        setKeyListener(placementListener);
    }

    @Override
    public void movementPerformed(int row, int col, IPiece piece)
    {
        currentPlayerMoved = true;
        gridModel.stopCarryingPiece();
        gridModel.syncPieces(controller::pieceAt);
        frame.repaint();
        setKeyListener(selectionListener);
    }

    @Override
    public void pieceUnselected()
    {
        gridModel.stopCarryingPiece();
        gridModel.syncPieces(controller::pieceAt);
        frame.repaint();
        setKeyListener(selectionListener);
    }

    @Override
    public void turnFinished(Player nextPlayer)
    {
        if (nextPlayer.equals(Player.PLAYER2)) {
            p1prevCoord = gridModel.getCursor();
            gridModel.setCursor(p2prevCoord);
        } else {
            p2prevCoord = gridModel.getCursor();
            gridModel.setCursor(p1prevCoord);
        }

        currentPlayerMoved = false;
        gridPanel.setCursorColor(Palette.color(nextPlayer));
        frame.repaint();
        setKeyListener(selectionListener);
    }
}
