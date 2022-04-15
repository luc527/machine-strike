package gamebuild;

import logic.Direction;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PiecePlacementView implements PiecePlacementObserver
{
    private final JFrame frame;

    private final MachineSelectionPanel p1machinePanel;
    private final MachineSelectionPanel p2machinePanel;
    private Player placingPlayer;

    public PiecePlacementView(GameBuildingController controller)
    {
        controller.attach(this);

        frame = new JFrame();
        var panel = new JPanel();
        frame.setContentPane(panel);

        panel.setLayout(new BorderLayout());

        this.p1machinePanel = new MachineSelectionPanel(new Color(32, 145, 100));
        this.p2machinePanel = new MachineSelectionPanel(new Color(145, 32, 100));
        panel.add(p1machinePanel, BorderLayout.LINE_START);
        panel.add(p2machinePanel, BorderLayout.LINE_END);

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
                        controller.selectMachineUnderCursor();
                        controller.switchPlacingPlayer();
                    }
                }
            });
        }

        placingPlayerSwitchedTo(placingPlayer);
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    public void setInitialPlacingPlayer(Player p)
    {
        this.placingPlayer = p;
    }

    private MachineSelectionPanel placingPlayerPanel()
    {
        return placingPlayer == Player.PLAYER1 ? p1machinePanel : p2machinePanel;
    }

    public void placingPlayerSwitchedTo(Player placingPlayer)
    {
        this.placingPlayer = placingPlayer;
        var panel = placingPlayerPanel();
        var other = panel == p1machinePanel ? p2machinePanel : p1machinePanel;
        panel.requestFocus();
        panel.showCursor(true);
        other.showCursor(false);
        panel.repaint();
        other.repaint();
    }

    public void machineCursorSetTo(String machineName)
    {
        var panel = placingPlayerPanel();
        panel.setCursorOver(machineName);
        panel.repaint();
    }

    public void machineUnderCursorSelected()
    {
        var panel = placingPlayerPanel();
        panel.setMachineUnderCursorSelected(true);
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
