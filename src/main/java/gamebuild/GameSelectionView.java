package gamebuild;

import assets.Boards;
import graphics.BoardIcon;
import logic.Board;
import logic.Player;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameSelectionView implements GameSelectionObserver
{
    private GameBuildingController controller;

    private JFrame frame;
    private JRadioButton player1radio;
    private JRadioButton player2radio;
    private Map<Board, JRadioButton> boardRadioMap;
    private JButton nextButton;

    public GameSelectionView(GameBuildingController controller)
    {
        this.controller = controller;
        this.controller.attach(this);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        frame.setContentPane(panel);

        //
        // Player selection
        //

        player1radio = new JRadioButton("Player 1");
        player2radio = new JRadioButton("Player 2");
        var playerGroup = new ButtonGroup();
        playerGroup.add(player1radio);
        playerGroup.add(player2radio);
        player1radio.addActionListener(e -> controller.selectStartingPlayer(Player.PLAYER1));
        player2radio.addActionListener(e -> controller.selectStartingPlayer(Player.PLAYER2));
        controller.selectStartingPlayer(Player.PLAYER1);

        var startingPlayerForm = new JPanel();
        startingPlayerForm.setLayout(new FlowLayout());
        startingPlayerForm.add(new JLabel("Starting player: "));
        startingPlayerForm.add(player1radio);
        startingPlayerForm.add(player2radio);
        panel.add(startingPlayerForm);

        //
        // Board selection
        //

        var boardSelectionPanel = new JPanel();
        boardSelectionPanel.setLayout(new FlowLayout());
        var boardButtonGroup = new ButtonGroup();
        boardRadioMap = new HashMap();
        boardSelectionPanel.add(new JLabel("Board:"));
        for (var board : Boards.all()) {
            var boardRadio = new JRadioButton();
            boardRadio.addActionListener(e -> controller.selectBoard(board));

            boardSelectionPanel.add(boardRadio);
            var iconLabel = new JLabel(new BoardIcon(board));
            boardSelectionPanel.add(iconLabel);
            iconLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { boardRadio.doClick(); }
            });

            boardButtonGroup.add(boardRadio);
            boardRadioMap.put(board, boardRadio);
        }
        controller.selectBoard(Boards.all().get(0));
        panel.add(boardSelectionPanel);

        //
        // Next stage
        //

        nextButton = new JButton("Next");
        nextButton.addActionListener(evt -> controller.confirmSelections());
        panel.add(nextButton);
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    public void selectedStartingPlayer(Player p)
    {
        if (p == Player.PLAYER1) player1radio.setSelected(true);
        else                     player2radio.setSelected(true);
    }

    public void selectedBoard(Board b)
    {
        boardRadioMap.get(b).setSelected(true);
    }

    private void setEnabled(boolean b)
    {
        player1radio.setEnabled(b);
        player2radio.setEnabled(b);

        for (var radio : boardRadioMap.values()) {
            radio.setEnabled(b);
        }

        nextButton.setEnabled(b);
    }

    public void selectionsConfirmed()
    {
        setEnabled(false);
        var piecePlacementView = new PiecePlacementView(this.controller);
        piecePlacementView.show();
        piecePlacementView.onClose(() -> setEnabled(true));
    }

}
