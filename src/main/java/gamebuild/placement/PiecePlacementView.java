package gamebuild.placement;

import boardgrid.BoardGridModel;
import boardgrid.BoardGridPanel;
import gamebuild.machinegrid.MachineGridModel;
import gamebuild.machinegrid.MachineGridPanel;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PiecePlacementView implements IPiecePlacementObserver
{
    private final JFrame frame;
    private final MachineGridPanel p1gridPanel;
    private final MachineGridPanel p2gridPanel;
    private final BoardGridPanel boardPanel;

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

        var boardModel = new BoardGridModel(con.getBoard(), con::getPieceAt);
        boardPanel = new BoardGridPanel(boardModel);

        panel.add(p1gridPanel, BorderLayout.LINE_START);
        panel.add(p2gridPanel, BorderLayout.LINE_END);
        panel.add(boardPanel, BorderLayout.CENTER);

        // TODO refactor all these callbacks, make some methods up, DRY etc

        p1gridPanel.onSelect(() -> {
            var selectedMachine = p1gridModel.selectMachineUnderCursor();
            if (!selectedMachine.isPresent()){
                System.out.println("TODO warn, piece depleted");
                return;
            }
            boardModel.startInteraction(
                    con.getInitialAvailablePosition(),
                    con.getAvailablePositions(),
                    selectedMachine.get()
            );
            boardPanel.setCursorColor(Player.PLAYER1.color());
            boardPanel.setFocused(true);
            p1gridPanel.setFocused(false);
        });

        p2gridPanel.onSelect(() -> {
            var selectedMachine = p2gridModel.selectMachineUnderCursor();
            if (!selectedMachine.isPresent()) {
                System.out.println("TODO warn, piece depleted");
                return;
            }
            boardModel.startInteraction(
                    con.getInitialAvailablePosition(),
                    con.getAvailablePositions(),
                    selectedMachine.get()
            );
            boardPanel.setCursorColor(Player.PLAYER2.color());
            boardPanel.setFocused(true);
            p2gridPanel.setFocused(false);
        });

        boardPanel.onCancel(() -> {
            var playerPanel = con.getPlacingPlayer() == Player.PLAYER1 ? p1gridPanel : p2gridPanel;
            playerPanel.setFocused(true);
            boardPanel.setFocused(false);
        });

        boardPanel.onConfirm(() -> {
            var coord = boardModel.getCursor();
            var machine = boardModel.getCarriedMachine();

            if (!con.placePiece(machine, coord)) {
                System.out.println("todo warn occupied");
                return;
            }

            var playerPanel = con.getPlacingPlayer() == Player.PLAYER1 ? p1gridPanel : p2gridPanel;
            playerPanel.setFocused(true);
            var other = playerPanel == p1gridPanel ? p2gridPanel : p1gridPanel;
            other.repaint();
            boardPanel.setFocused(false);
        });
    }

    public void show()
    {
        frame.pack();
        // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        p1gridPanel.setFocused(true);
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
