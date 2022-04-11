package gameconfig;

import logic.Board;
import logic.Piece;
import logic.Player;

import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameConfigurationView implements GameConfigurationObserver
{
    private GameConfigurationController controller;

    private JFrame frame;
    private JRadioButton player1radio;
    private JRadioButton player2radio;
    private Map<Board, JRadioButton> boardRadios;

    public GameConfigurationView(GameConfigurationController controller, List<Board> availableBoards)
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
        // Let's ignore how the boards are going to be shown for now
        var boardForm = new JPanel();
        boardForm.setLayout(new FlowLayout());
        var boardGroup = new ButtonGroup();
        boardRadios = new HashMap();
        boardForm.add(new JLabel("Board:"));
        for (var board : availableBoards) {
            var boardRadio = new JRadioButton("<html>"+board.toString().replaceAll("\n", "<br>")+"</html>");
            boardRadio.addActionListener(e -> controller.selectBoard(board));
            boardForm.add(boardRadio);
            boardGroup.add(boardRadio);
            boardRadios.put(board, boardRadio);
        }
        controller.selectBoard(availableBoards.get(0));
        panel.add(boardForm);

        // TODO button for opening the piece placement screen
        //  and make the piece placement screen

    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    public void startingPlayerSetTo(Player p)
    {
        if (p == Player.PLAYER1) player1radio.setSelected(true);
        else                     player2radio.setSelected(true);
    }

    @Override
    public void selectedBoard(Board b)
    {
        boardRadios.get(b).setSelected(true);
    }

    @Override
    public void piecePlaced(Piece p, int row, int col)
    {

    }

    @Override
    public void placingPlayerSetTo(Player p)
    {

    }
}
