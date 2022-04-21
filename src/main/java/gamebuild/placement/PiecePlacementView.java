package gamebuild.placement;

import boardgrid.BoardGridModel;
import boardgrid.BoardGridPanel;
import gamebuild.machinegrid.MachineGridModel;
import gamebuild.machinegrid.MachineGridPanel;
import logic.ICoord;
import logic.Piece;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class PiecePlacementView implements IPiecePlacementObserver
{
    private final JFrame frame;
    private final JLabel warnLabel;

    private final MachineGridPanel p1gridPanel;
    private final MachineGridPanel p2gridPanel;

    private final BoardGridModel boardModel;
    private final BoardGridPanel boardPanel;

    private Thread currentWarnThread;
    private int warnCounter = 0;

    public PiecePlacementView(IPiecePlacementController con)
    {
        con.attach(this);

        frame = new JFrame();
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.setContentPane(panel);

        var p1inventory = con.getPlayerInventory(Player.PLAYER1);
        var p2inventory = con.getPlayerInventory(Player.PLAYER2);

        var p1gridModel = new MachineGridModel(p1inventory);
        var p2gridModel = new MachineGridModel(p2inventory);

        p1gridPanel = new MachineGridPanel(p1gridModel, Player.PLAYER1.color());
        p2gridPanel = new MachineGridPanel(p2gridModel, Player.PLAYER2.color());

        boardModel = new BoardGridModel(con.getBoard(), con::getPieceAt);
        boardPanel = new BoardGridPanel(boardModel);

        // TODO figure out how to make warning font bigger
        warnLabel = new JLabel(" ");
        warnLabel.setForeground(Color.RED);

        p1gridPanel.onSelect(() -> {
            var machine = p1gridModel.machineUnderCursor();
            var ok = con.selectMachine(machine);
            if (!ok) {
                warn("You don't have any more pieces of this type!");
                return;
            }
        });

        p2gridPanel.onSelect(() -> {
            var machine = p2gridModel.machineUnderCursor();
            if (!con.selectMachine(machine)) {
                warn("You don't have any more pieces of this type!");
            }
        });

        boardPanel.onCancel(() -> con.cancelSelection());

        boardPanel.onConfirm(() -> {
            var coord = boardModel.getCursor();
            var machine = boardModel.getCarriedMachine();
            if (!con.placePiece(machine, coord)) {
                warn("This space is already occupied!");
            }
        });

        panel.add(p1gridPanel, BorderLayout.LINE_START);
        panel.add(p2gridPanel, BorderLayout.LINE_END);
        panel.add(boardPanel, BorderLayout.CENTER);
        panel.add(warnLabel, BorderLayout.PAGE_END);
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

    public void show(Player firstPlayer)
    {
        frame.pack();
        frame.setVisible(true);
        playerMachinePanel(firstPlayer).setFocused(true);
    }

    @Override
    public void machineSelected(String machine, Player player, ICoord initialPos, Set<ICoord> availablePos)
    {
        boardModel.startInteraction(initialPos, availablePos, machine);
        boardPanel.setCursorColor(player.color());
        boardPanel.setFocused(true);
        playerMachinePanel(player).setFocused(false);
    }

    @Override
    public void selectionCancelled(Player player)
    {
        boardPanel.setFocused(false);
        playerMachinePanel(player).setFocused(true);
    }

    @Override
    public void piecePlaced(Piece piece, ICoord pos)
    {
        playerMachinePanel(piece.player()).setFocused(false);
        var other = playerMachinePanel(piece.player().next());
        other.setFocused(true);
        other.repaint();
        boardPanel.setFocused(false);
    }

    public void onClose(Runnable r)
    {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                r.run();
            }
        });
    }
}
