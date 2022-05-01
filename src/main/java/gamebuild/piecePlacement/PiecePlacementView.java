package gamebuild.piecePlacement;

import components.boardgrid.BoardGridModel;
import components.boardgrid.BoardGridPanel;
import components.machinegrid.MachineGridModel;
import components.machinegrid.MachineGridPanel;
import components.Palette;
import logic.Coord;
import logic.Machine;
import logic.Piece;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class PiecePlacementView implements IPiecePlacementObserver
{
    private final JFrame frame;
    private final JLabel warnLabel;

    private final IPiecePlacementController con;

    private final MachineGridPanel p1gridPanel;
    private final MachineGridPanel p2gridPanel;

    private final BoardGridModel boardModel;
    private final BoardGridPanel boardPanel;

    private final JButton startGameButton;

    private Thread currentWarnThread;
    private int warnCounter = 0;

    public PiecePlacementView(IPiecePlacementController con)
    {
        con.attach(this);
        this.con = con;

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.setContentPane(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // The inventories encapsulate the logic around each player having a certain amount
        // of certain machines to be placed in the board
        var p1inventory = con.getPlayerInventory(Player.PLAYER1);
        var p2inventory = con.getPlayerInventory(Player.PLAYER2);

        // The grid model (logically, not visually) organizes those machines in a grid
        // on which a cursor can move
        var p1gridModel = new MachineGridModel(p1inventory.getMachines(), 3);
        var p2gridModel = new MachineGridModel(p2inventory.getMachines(), 3);

        // Finally, the grid panels are responsible for showing the machines, the cursor,
        // and the amounts of each machine; they are the visuals for the aforementioned models and inventories
        p1gridPanel = new MachinePlacementGridPanel(p1gridModel, p1inventory, Palette.p1color);
        p2gridPanel = new MachinePlacementGridPanel(p2gridModel, p2inventory, Palette.p2color);

        boardModel = new BoardGridModel(con.getBoard(), con::getPieceAt);
        boardPanel = new BoardGridPanel(boardModel);

        warnLabel = new JLabel(" ");
        warnLabel.setForeground(Color.RED);

        p1gridPanel.onPressEnter(() -> {
            var machine = p1gridModel.machineAt(p1gridModel.cursor());
            var ok = con.selectMachine(machine);
            if (!ok) {
                warn("You don't have any more pieces of this type!");
            }
        });

        p2gridPanel.onPressEnter(() -> {
            var machine = p2gridModel.machineAt(p2gridModel.cursor());
            if (!con.selectMachine(machine)) {
                warn("You don't have any more pieces of this type!");
            }
        });

        boardPanel.onCancel(con::cancelSelection);

        boardPanel.onConfirm(() -> {
            var coord = boardModel.getCursor();
            var machine = boardModel.getCarriedMachine();
            var direction = boardModel.getCarriedMachineDirection();
            if (!con.placeMachine(coord, machine, direction)) {
                warn("This space is already occupied!");
            }
        });

        startGameButton = new JButton("Start game");
        startGameButton.addActionListener(e -> con.startGame());
        startGameButton.setEnabled(false);

        var bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(warnLabel, BorderLayout.NORTH);
        bottomRow.add(startGameButton, BorderLayout.SOUTH);

        panel.add(p1gridPanel, BorderLayout.LINE_START);
        panel.add(p2gridPanel, BorderLayout.LINE_END);
        panel.add(boardPanel, BorderLayout.CENTER);
        panel.add(bottomRow, BorderLayout.PAGE_END);
    }

    private void warn(String s)
    {
        warnCounter++;
        warnLabel.setText(String.format("<html>%s <b>(%d)</b></html>", s, warnCounter));
        warnLabel.repaint();
        if (currentWarnThread != null) {
            currentWarnThread.interrupt();
        }
        currentWarnThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                warnCounter = 0;
                warnLabel.setText(" ");
                warnLabel.repaint();
            } catch (InterruptedException ignored) {}
        });
        currentWarnThread.start();
    }

    private MachineGridPanel playerMachinePanel(Player p)
    {
        return p == Player.PLAYER1 ? p1gridPanel : p2gridPanel;
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
        playerMachinePanel(con.getFirstPlayer()).setFocused(true);
    }

    @Override
    public void pieceSelected(Machine machine, Player player, Coord initialPos, Set<Coord> availablePos)
    {
        boardModel.startInteraction(initialPos, availablePos, machine);
        boardPanel.setCursorColor(Palette.color(player));
        boardPanel.setFocused(true);
        playerMachinePanel(player).setFocused(false);
    }

    @Override
    public void selectionCancelled(Player player)
    {
        boardModel.endInteraction();
        boardPanel.setFocused(false);
        playerMachinePanel(player).setFocused(true);
    }

    @Override
    public void piecePlaced(Piece piece, Coord pos, Player nextPlayer)
    {
        playerMachinePanel(nextPlayer.prev()).setFocused(false);
        playerMachinePanel(nextPlayer).setFocused(true);
        boardModel.endInteraction();
        boardPanel.setFocused(false);
    }

    @Override
    public void allPiecesPlaced()
    {
        p1gridPanel.setFocused(false);
        p2gridPanel.setFocused(false);
        startGameButton.setEnabled(true);
        startGameButton.requestFocusInWindow();
    }
}
