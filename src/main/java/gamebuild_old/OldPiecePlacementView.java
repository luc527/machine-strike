package gamebuild_old;

import logic.CoordState;
import logic.Direction;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


// TODO refactor this whole thing later, it's messy
//  consider especially not having the cursor level of granularity in the interfaces
//  on the controller only selectPiece, placePiece and so on

public class OldPiecePlacementView implements PiecePlacementObserver
{
    private final JFrame frame;

    private final GameBuildingController controller;

    private final MachineSelectionPanel p1machinePanel;
    private final MachineSelectionPanel p2machinePanel;
    private final BoardPanel boardPanel;
    private final JLabel warnLabel;
    private List<CoordState> availablePlacements;

    public OldPiecePlacementView(GameBuildingController controller)
    {
        controller.attach(this);
        this.controller = controller;

        frame = new JFrame();
        var panel = new JPanel();
        frame.setContentPane(panel);

        panel.setLayout(new BorderLayout());

        //
        // Machine selection panels
        // for the players to select the machines to put on the board
        //

        p1machinePanel = new MachineSelectionPanel(
                controller.getPlayerInventory(Player.PLAYER1),
                Player.PLAYER1.color()
        );
        p2machinePanel = new MachineSelectionPanel(
                controller.getPlayerInventory(Player.PLAYER2),
                Player.PLAYER2.color()
        );
        panel.add(p1machinePanel, BorderLayout.LINE_START);
        panel.add(p2machinePanel, BorderLayout.LINE_END);

        var that = this;
        for (var machinePanel : new MachineSelectionPanel[]{p1machinePanel, p2machinePanel}) {
            machinePanel.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                    var k = e.getKeyCode();
                    var c = e.getKeyChar();
                    if (k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN || k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT) {
                        String machineName = "";
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_UP -> machineName = machinePanel.moveCursor(Direction.NORTH);
                            case KeyEvent.VK_DOWN -> machineName = machinePanel.moveCursor(Direction.SOUTH);
                            case KeyEvent.VK_LEFT -> machineName = machinePanel.moveCursor(Direction.WEST);
                            case KeyEvent.VK_RIGHT -> machineName = machinePanel.moveCursor(Direction.EAST);
                        }
                        controller.setMachineCursorOver(machineName);
                    } else if (c == KeyEvent.VK_ENTER) {
                        if (!controller.selectMachine(machinePanel.machineUnderCursor())) {
                            that.warn("You don't have any more pieces of this type!");
                        }
                    }
                }
            });
        }

        focusOn(controller.getStartingPlayer());

        //
        // Board panel
        // where the players actually put their pieces on
        //

        boardPanel = new BoardPanel(controller.getBoard(), controller::pieceAt);
        panel.add(boardPanel, BorderLayout.CENTER);

        boardPanel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                var k = e.getKeyCode();
                var c = e.getKeyChar();
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_DOWN || k == KeyEvent.VK_LEFT) {
                    var coord = new CoordState(0, 0);
                    switch (k) {
                        case KeyEvent.VK_UP -> coord = boardPanel.moveCursor(Direction.NORTH);
                        case KeyEvent.VK_RIGHT -> coord = boardPanel.moveCursor(Direction.EAST);
                        case KeyEvent.VK_DOWN -> coord = boardPanel.moveCursor(Direction.SOUTH);
                        case KeyEvent.VK_LEFT -> coord = boardPanel.moveCursor(Direction.WEST);
                    }
                    controller.setBoardCursor(coord);
                } else if (c == KeyEvent.VK_ESCAPE) {
                    controller.cancelCurrentPlacement();
                } else if (c == KeyEvent.VK_ENTER) {
                    controller.confirmCurrentPlacement();
                }
            }
        });

        warnLabel = new JLabel(" ");
        warnLabel.setForeground(Color.RED);
        // TODO better header layout, not just warnings (players etc.)
        panel.add(warnLabel, BorderLayout.SOUTH);
    }

    private void warn(String s)
    {
        warnLabel.setText(s);
        warnLabel.repaint();
        new Thread(() -> {
            try {
                Thread.sleep(2500);
                warnLabel.setText(" ");
                warnLabel.repaint();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }).start();
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    private void focusOn(Player player)
    {
        var panel = player == Player.PLAYER1 ? p1machinePanel : p2machinePanel;
        var other = player == Player.PLAYER1 ? p2machinePanel : p1machinePanel;
        panel.setFocused(true);
        panel.repaint();
        other.setFocused(false);
        other.repaint();
    }

    @Override
    public void boardCursorOver(CoordState coord)
    {
        boardPanel.setCursor(coord);
        boardPanel.repaint();
    }

    private MachineSelectionPanel placingPlayerPanel()
    {
        return controller.placingPlayer() == Player.PLAYER1 ? p1machinePanel : p2machinePanel;
    }

    @Override
    public void machineCursorOver(String machineName)
    {
        var panel = placingPlayerPanel();
        panel.setCursorOver(machineName);
        panel.repaint();
    }

    @Override
    public void machineSelected(String machineName)
    {
        var panel = placingPlayerPanel();
        panel.setFocused(false);
        panel.repaint();

        var available = controller.availablePlacements();
        boardPanel.setAvailablePositions(available);
        boardPanel.setCursor(available.get(0));
        boardPanel.setCursorColor(controller.placingPlayer().color());
        boardPanel.setFocused(true);
        boardPanel.carryMachine(machineName);
        boardPanel.repaint();
    }

    @Override
    public void currentPlacementCancelled()
    {
        boardPanel.setFocused(false);
        boardPanel.setAvailablePositions(List.of());
        boardPanel.repaint();
        var machinePanel = placingPlayerPanel();
        machinePanel.setFocused(true);
        machinePanel.repaint();
    }

    @Override
    public void currentPlacementConfirmed()
    {
        boardPanel.setFocused(false);
        boardPanel.setAvailablePositions(List.of());
        boardPanel.repaint();
        focusOn(controller.placingPlayer());
    }

    @Override
    public void currentPlacementFailed()
    {
        warn("That tile is already occupied!");
    }

    public void onClose(Runnable r)
    {
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                r.run();
                frame.dispose();
            }
        });
    }
}
