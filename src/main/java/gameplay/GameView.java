package gameplay;

import components.MachineStatsPanel;
import components.Palette;
import gameplay.playState.PlayState;
import logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;

public class GameView implements GameObserver
{
    private final IGameState game;

    private final JFrame frame;
    private final GameGridModel gridModel;
    private final GameGridPanel gridPanel;
    private final InfoPanel infoPanel;
    private final VictoryPointsPanel vpPanel;

    private Coord p1prevCoord = Coord.create(0, 0);
    private Coord p2prevCoord = Coord.create(0, 0);

    private PlayState playState;

    private KeyListener currentKeyListener;

    public GameView(IGameController controller)
    {
        controller.attach(this);

        this.game = controller.getGameState();

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);

        gridModel = new GameGridModel(game.board(), game);
        gridPanel = new GameGridPanel(gridModel);

        infoPanel = new InfoPanel();
        panel.add(infoPanel, BorderLayout.LINE_START);

        vpPanel = new VictoryPointsPanel();
        panel.add(vpPanel, BorderLayout.PAGE_START);

        // this depends on vpPanel, infoPanel being instantiated
        updateState(PlayState.initialState(this, game, controller, gridModel, frame));

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
        infoPanel.display(playState.getInfoPanelFlags());
        vpPanel.setVP(game.victoryPoints(Player.PLAYER1), game.victoryPoints(Player.PLAYER2));
    }

    private void setKeyListener(KeyListener to)
    {
        gridPanel.removeKeyListener(currentKeyListener);
        gridPanel.addKeyListener(to);
        currentKeyListener = to;
    }

    @Override
    public void start(Player firstPlayer)
    {
        frame.pack();
        frame.setVisible(true);
        gridPanel.setCursorColor(Palette.color(firstPlayer));
        gridPanel.setFocused(true);
    }

    @Override
    public void pieceSelected(int row, int col, IPiece piece)
    {
        gridModel.carryPieceFrom(Coord.create(row, col));
        frame.repaint();
        playState.startMove();
    }

    public void updateState(PlayState state)
    {
        this.playState = state;
        setKeyListener(state.getKeyListener());
        updateInfo();
    }

    @Override
    public void gameStateChanged()
    {
        gridModel.stopCarryingPiece();
        gridModel.syncPieces();
        frame.repaint();
        playState.finishMove();
    }

    @Override
    public void pieceUnselected()
    {
        gridModel.stopCarryingPiece();
        gridModel.syncPieces();
        frame.repaint();
        playState.finishMove();
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

        gridPanel.setCursorColor(Palette.color(nextPlayer));
        frame.repaint();
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
