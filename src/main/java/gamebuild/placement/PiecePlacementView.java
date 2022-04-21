package gamebuild.placement;

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

        p1gridPanel = new MachineGridPanel(p1gridModel, Player.PLAYER1.color(), () -> System.out.println("selected (TODO)!"));;
        p2gridPanel = new MachineGridPanel(p2gridModel, Player.PLAYER2.color(), () -> System.out.println("selected (TODO)!"));;

        panel.add(p1gridPanel, BorderLayout.LINE_START);
        panel.add(p2gridPanel, BorderLayout.LINE_END);
    }

    public void show()
    {
        frame.pack();
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
