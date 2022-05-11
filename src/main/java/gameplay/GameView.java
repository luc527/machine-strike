package gameplay;

import components.Palette;
import components.boardgrid.BoardGridModel;
import components.boardgrid.BoardGridPanel;
import logic.Player;

import javax.swing.*;
import java.awt.*;

/**
 * TODO!!! refactor the BoardGridModel and BoardGridPanel to be a little more general,
 *   so I can use them for both piece placement and for the game itself
 */

public class GameView implements GameObserver
{
    private final IGameController controller;
    private final JFrame frame;
    private final JPanel content;

    public GameView(IGameController controller)
    {
        controller.attach(this);
        this.controller = controller;

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        content = new JPanel();
        content.setLayout(new BorderLayout());
        frame.setContentPane(content);
    }

    @Override
    public void start(Player firstPlayer, BoardGridModel board)
    {
        var boardPanel = new BoardGridPanel(board);
        content.add(boardPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        boardPanel.setCursorColor(Palette.color(firstPlayer));
        boardPanel.setFocused(true);
        boardPanel.onConfirm(() -> {
            var response = controller.selectPiece();
            if (response != PieceSelectionResponse.OK) {
                System.out.println("TODO warning label. " + response);
            }
        });
    }
}
