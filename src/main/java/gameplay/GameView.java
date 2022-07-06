package gameplay;

import components.MachineStatsPanel;
import components.Palette;
import logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Function;

// TODO use state pattern for selectionListener/placementListener

public class GameView implements GameObserver
{
    private final IGameState game;

    private final JFrame frame;
    private final GameGridModel gridModel;
    private final GameGridPanel gridPanel;
    private final InfoPanel infoPanel;
    private final VictoryPointsPanel vpPanel;

    private boolean currentPlayerMoved = false;
    private Coord p1prevCoord = Coord.create(0, 0);
    private Coord p2prevCoord = Coord.create(0, 0);

    private KeyListener currentKeyListener;
    private final KeyListener selectionListener;
    private final KeyListener placementListener;  // TODO bad name, find a better one

    public GameView(IGameController controller)
    {
        controller.attach(this);

        this.game = controller.getGameState();

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);

        selectionListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var k = e.getKeyCode();
                var c = e.getKeyChar();
                if (k == KeyEvent.VK_ENTER && !game.playerRanOutOfMoves()) {
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
                    controller.performMovement(row, col, dir);
                } else if (c == 'k') {
                    controller.performAttack(row, col, dir);
                }else if (gridModel.isCarryingPiece()) {
                    if      (c == 'q') gridModel.rotateCarriedPiece(false);
                    else if (c == 'e') gridModel.rotateCarriedPiece(true);
                    frame.repaint();
                }
            }
        };

        gridModel = new GameGridModel(game.board(), game::pieceAt);
        gridPanel = new GameGridPanel(gridModel);

        gridModel.setConflictResultFunction(game::getConflictDamages);

        infoPanel = new InfoPanel();
        infoPanel.display(InfoPanel.ENTER_TO_SELECT);
        panel.add(infoPanel, BorderLayout.LINE_START);

        vpPanel = new VictoryPointsPanel();
        panel.add(vpPanel, BorderLayout.PAGE_START);

        var machStats = new MachineStatsPanel();
        panel.add(machStats, BorderLayout.LINE_END);

        gridModel.onMove(() -> {
            updateInfo();
            Machine mach = null;
            if (gridModel.isCarryingPiece()) {
                mach = gridModel.getCarriedPiece().machine();
            } else {
                var piece = gridModel.pieceAt(gridModel.getCursor());
                if (piece != null) mach = piece.machine();
            }
            machStats.setMachine(mach);
        });

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

            var coord = gridModel.getCursor();
            var piece = gridModel.getCarriedPiece();
            var dir   = gridModel.getCarriedPieceDirection();
            var attack = piece.machine().attackType();

            // TODO also check whether the piece is not running, since we can't run AND attack
            var isAttacking = attack.isAttacking(gridModel::pieceAt, coord, piece, dir);
            var isRunning   = gridModel.isReachable(coord.row(), coord.col()).inRunning();

            if (isAttacking && !isRunning) {
                flags |= InfoPanel.K_TO_ATTACK;
            }
        }
        infoPanel.display(flags);

        vpPanel.setVP(game.victoryPoints(Player.PLAYER1), game.victoryPoints(Player.PLAYER2));
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
        gridModel.syncPieces(game::pieceAt);
        frame.repaint();
        setKeyListener(selectionListener);
    }

    @Override
    public void pieceUnselected()
    {
        gridModel.stopCarryingPiece();
        gridModel.syncPieces(game::pieceAt);
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

    @Override
    public void gameWonBy(Player winner)
    {
        gridPanel.setFocused(false);
        setKeyListener(new KeyAdapter() {});
        var msg = String.format("The winner is %s!", winner.equals(Player.PLAYER1) ? "Player 1" : "Player 2");
        JOptionPane.showMessageDialog(frame, msg, "Winner!", JOptionPane.INFORMATION_MESSAGE);
    }
}
