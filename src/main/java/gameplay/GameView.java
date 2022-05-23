package gameplay;

import components.Palette;
import logic.Coord;
import logic.GameState;
import logic.Piece;
import logic.Player;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

public class GameView implements GameObserver
{
    private final IGameController controller;

    private final JFrame frame;
    private final GameGridModel gridModel;
    private final GameGridPanel gridPanel;

    private final KeyListener selectionListener;
    private final KeyListener placementListener;  // TODO bad name, find a better one

    public GameView(IGameController controller)
    {
        this.controller = controller;
        controller.attach(this);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel();
        frame.setContentPane(panel);

        selectionListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
                var cursor = gridModel.getCursor();
                controller.selectPiece(cursor.row(), cursor.col());
            }
        };

        placementListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                if (k == KeyEvent.VK_BACK_SPACE) controller.unselectPiece();
            }
        };

        gridModel = new GameGridModel(controller.getBoard(), controller::pieceAt);
        gridPanel = new GameGridPanel(gridModel);
        gridPanel.addKeyListener(selectionListener);

        panel.add(gridPanel);
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
    public void pieceSelected(int row, int col, Piece piece, Set<Coord> availablePositions)
    {
        gridPanel.removeKeyListener(selectionListener);
        gridModel.setAvailablePositions(availablePositions);
        frame.repaint();
        gridPanel.addKeyListener(placementListener);
    }

    @Override
    public void pieceUnselected()
    {
        gridPanel.removeKeyListener(placementListener);
        gridModel.setAvailablePositions(Set.of());
        frame.repaint();
        gridPanel.addKeyListener(selectionListener);
    }
}
