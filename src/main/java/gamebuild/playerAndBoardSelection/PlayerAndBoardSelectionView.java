package gamebuild.playerAndBoardSelection;

import gamebuild.machineSelection.IMachineSelectionController;
import gamebuild.machineSelection.MachineSelectionView;
import graphics.BoardIcon;
import logic.Board;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayerAndBoardSelectionView implements IPlayerAndBoardSelectionObserver
{
    private final JFrame frame;
    private final JRadioButton p1radio;
    private final JRadioButton p2radio;
    private final JButton nextButton;
    private final List<JRadioButton> boardRadios;

    private Board selectedBoard;

    public PlayerAndBoardSelectionView(IPlayerAndBoardSelectionController con)
    {
        con.attach(this);

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        frame.setContentPane(panel);

        //
        // Player selection
        //

        p1radio = new JRadioButton("Player 1");
        p2radio = new JRadioButton("Player 2");
        var playerButtonGroup = new ButtonGroup();
        playerButtonGroup.add(p1radio);
        playerButtonGroup.add(p2radio);
        p1radio.doClick();

        var playerForm = new JPanel();
        playerForm.setLayout(new FlowLayout());
        playerForm.add(new JLabel("Starting player: "));
        playerForm.add(p1radio);
        playerForm.add(p2radio);
        panel.add(playerForm);

        //
        // Board selection
        //

        boardRadios = new ArrayList<>();

        var boardPanel = new JPanel();
        boardPanel.setLayout(new FlowLayout());
        boardPanel.add(new JLabel("Board: "));
        panel.add(boardPanel);

        var boardButtonGroup = new ButtonGroup();

        for (var board : con.getBoardList()) {
            var radio = new JRadioButton();
            radio.addActionListener(e -> selectedBoard = board);

            var boardLabel = new JLabel(new BoardIcon(board));
            boardLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    radio.doClick();
                }
            });

            boardPanel.add(radio);
            boardPanel.add(boardLabel);

            boardButtonGroup.add(radio);

            boardRadios.add(radio);
        }
        boardRadios.get(0).doClick();


        //
        // Next stage
        //

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            var player = p1radio.isSelected() ? Player.PLAYER1 : Player.PLAYER2;
            con.select(player, selectedBoard);
        });
        panel.add(nextButton);
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void selectionFinished(Player startingPlayer, Board board, IMachineSelectionController nextCon)
    {
        var nextView = new MachineSelectionView(nextCon);
        nextView.show(nextCon.getFirstPlayer());
        frame.dispose();
    }

}
