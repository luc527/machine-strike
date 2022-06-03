package gameplay;

import components.Palette;
import logic.Coord;
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

    private KeyListener currentKeyListener;

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
                if (!controller.selectPiece(cursor.row(), cursor.col())) return;
                gridModel.carryPieceFrom(cursor);
            }
        };

        placementListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                var cursor = gridModel.getCursor();
                if (k == KeyEvent.VK_BACK_SPACE || k == KeyEvent.VK_ESCAPE) {
                    controller.unselectPiece();
                } else if (k == KeyEvent.VK_ENTER) {
                    controller.placePiece(cursor.row(), cursor.col(), gridModel.getCarriedPieceDirection());
                } else if (gridModel.isCarryingPiece()) {
                    if      (c == 'q') gridModel.rotateCarriedPiece(false);
                    else if (c == 'e') gridModel.rotateCarriedPiece(true);
                    frame.repaint();
                }
            }
        };

        gridModel = new GameGridModel(controller.getBoard(), controller::pieceAt);
        gridPanel = new GameGridPanel(gridModel);

        panel.add(gridPanel);
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
        setKeyListener(selectionListener);
    }

    @Override
    public void pieceSelected(int row, int col, Piece piece, Set<Coord> availablePositions)
    {
        gridModel.carryPieceFrom(Coord.create(row, col));
        gridModel.setAvailablePositions(availablePositions);
        frame.repaint();
        setKeyListener(placementListener);
    }

    @Override
    public void piecePlaced(int row, int col, Piece piece)
    {
        gridModel.setAvailablePositions(Set.of());
        gridModel.stopCarryingPiece();
        gridModel.syncPieces(controller::pieceAt);
        frame.repaint();
        setKeyListener(selectionListener);
    }

    @Override
    public void pieceUnselected()
    {
        gridModel.setAvailablePositions(Set.of());
        gridModel.stopCarryingPiece();
        gridModel.syncPieces(controller::pieceAt);
        frame.repaint();
        setKeyListener(selectionListener);
    }
}
