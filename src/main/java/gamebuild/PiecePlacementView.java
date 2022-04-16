package gamebuild;

import logic.Coord;
import logic.Direction;
import logic.Machine;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;





// TODO separate setFocues() into requestFocus() and showCursor() and repaint() again, they're different things!

public class PiecePlacementView implements PiecePlacementObserver
{
    private final JFrame frame;

    private final Color p1color = new Color(32, 100, 145);
    private final Color p2color = new Color(145, 32, 100);

    private final GameBuildingController controller;
    private final MachineSelectionPanel p1machinePanel;
    private final MachineSelectionPanel p2machinePanel;
    private final BoardPanel boardPanel;

    public PiecePlacementView(GameBuildingController controller)
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

        p1machinePanel = new MachineSelectionPanel(p1color);
        p2machinePanel = new MachineSelectionPanel(p2color);
        panel.add(p1machinePanel, BorderLayout.LINE_START);
        panel.add(p2machinePanel, BorderLayout.LINE_END);

        for (var machinePanel : new MachineSelectionPanel[]{p1machinePanel, p2machinePanel}) {
            machinePanel.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                    System.out.println(e);
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
                        controller.selectMachine(machinePanel.machineUnderCursor());
                    }
                }
            });
        }

        focusOn(controller.getStartingPlayer());

        //
        // Board panel
        // where the players actually put their pieces on
        //

        boardPanel = new BoardPanel(controller.getBoard());
        panel.add(boardPanel, BorderLayout.CENTER);

        boardPanel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                var k = e.getKeyCode();
                var c = e.getKeyChar();
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_DOWN || k == KeyEvent.VK_LEFT) {
                    var coord = new Coord(0, 0);
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
        other.setFocused(false);
    }

    @Override
    public void placingPlayerSwitchedTo(Player placingPlayer)
    {
        focusOn(placingPlayer);
    }

    @Override
    public void boardCursorMovedOver(Coord coord)
    {
        boardPanel.setCursorOver(coord);
        boardPanel.repaint();
    }

    private MachineSelectionPanel placingPlayerPanel()
    {
        return controller.getPlacingPlayer() == Player.PLAYER1 ? p1machinePanel : p2machinePanel;
    }

    @Override
    public void currentPlacementCancelled()
    {
        boardPanel.setFocused(false);
        var machinePanel = placingPlayerPanel();
        machinePanel.setFocused(true);
        machinePanel.setMachineUnderCursorSelected(false);
    }

    @Override
    public void currentPlacementConfirmed()
    {
        boardPanel.setFocused(false);
        focusOn(controller.getPlacingPlayer());
    }

    @Override
    public void machineCursorSetTo(String machineName)
    {
        var panel = placingPlayerPanel();
        panel.setCursorOver(machineName);
        panel.repaint();
    }

    @Override
    public void machineSelected(String machineName)
    {
        var panel = placingPlayerPanel();
        panel.setMachineUnderCursorSelected(true);
        panel.setFocused(false);
        boardPanel.setCursorColor(p1color);
        boardPanel.setFocused(true);
        boardPanel.carryMachine(machineName);
        panel.repaint();
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
