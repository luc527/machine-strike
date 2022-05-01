package gamebuild.machineSelection;

import assets.Machines;
import gamebuild.MachineInventory;
import components.machinegrid.MachineGridModel;
import gamebuild.piecePlacement.IPiecePlacementController;
import gamebuild.piecePlacement.PiecePlacementView;
import components.MachineStatsPanel;
import components.Palette;
import logic.Machine;
import logic.Player;

import javax.swing.*;
import java.awt.*;

public class MachineSelectionView implements IMachineSelectionObserver
{
    private final IMachineSelectionController con;

    private final JFrame frame;

    private final JPanel p1container;
    private final JPanel p2container;

    private final MachineGridModel p1gridModel;
    private final MachineGridModel p2gridModel;

    private final MachineSelectionGridPanel p1machSelPanel;
    private final MachineSelectionGridPanel p2machSelPanel;

    private JButton p1doneButton;
    private JButton p2doneButton;

    private JLabel p1vpCounter;
    private JLabel p2vpCounter;

    private MachineStatsPanel stats;

    private JLabel p1warn;
    private JLabel p2warn;
    private Thread currWarnThread;

    public MachineSelectionView(IMachineSelectionController con)
    {
        con.attach(this);
        this.con = con;

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var panel = new JPanel(new BorderLayout(10, 10));
        frame.setContentPane(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p1gridModel = new MachineGridModel(Machines.all(), 5);
        p2gridModel = new MachineGridModel(Machines.all(), 5);

        var p1machSelModel = con.getPlayerMachineSelection(Player.PLAYER1);
        var p2machSelModel = con.getPlayerMachineSelection(Player.PLAYER2);
        p1machSelPanel = new MachineSelectionGridPanel(p1machSelModel, p1gridModel, Palette.p1color);
        p2machSelPanel = new MachineSelectionGridPanel(p2machSelModel, p2gridModel, Palette.p2color);

        p1container = createPlayerPanel(Player.PLAYER1, p1machSelPanel);
        p2container = createPlayerPanel(Player.PLAYER2, p2machSelPanel);

        p1machSelPanel.onMoveCursor(() -> stats.setMachine(p1gridModel.machineUnderCursor()).repaint());
        p1machSelPanel.onPressEnter(() -> con.selectMachine(Player.PLAYER1, p1gridModel.machineUnderCursor()));
        p1machSelPanel.onPressBackspace(() -> con.deselectMachine(Player.PLAYER1, p1gridModel.machineUnderCursor()));

        p2machSelPanel.onMoveCursor(() -> stats.setMachine(p2gridModel.machineUnderCursor()).repaint());
        p2machSelPanel.onPressEnter(() -> con.selectMachine(Player.PLAYER2, p2gridModel.machineUnderCursor()));
        p2machSelPanel.onPressBackspace(() -> con.deselectMachine(Player.PLAYER2, p2gridModel.machineUnderCursor()));


        var spacer = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                var dim = super.getPreferredSize();
                return new Dimension(dim.width, 48);
            }
        };
        spacer.add(new JLabel("<html>ENTER select<br>BACKSPACE de-select</html>"));

        panel.add(p1container, BorderLayout.NORTH);
        panel.add(spacer, BorderLayout.CENTER);
        panel.add(p2container, BorderLayout.SOUTH);
    }

    private JPanel createPlayerPanel(Player player, MachineSelectionGridPanel machSelPanel)
    {
        var panel = new JPanel(new BorderLayout());
        panel.add(machSelPanel, BorderLayout.CENTER);
        var bottom = new JPanel();
        var vpCounter = new JLabel(String.format("VP: 0/%d", con.getPlayerMachineSelection(player).maxVictoryPoints()));
        bottom.add(vpCounter);
        var doneButton = new JButton("Done");
        doneButton.addActionListener(e -> con.playerDone(player));
        doneButton.setEnabled(false);
        bottom.add(doneButton);
        panel.add(bottom, BorderLayout.SOUTH);
        var warn = new JLabel(" ");
        warn.setForeground(Color.RED);
        panel.add(warn, BorderLayout.NORTH);

        if (player == Player.PLAYER1) {
            p1vpCounter = vpCounter;
            p1doneButton = doneButton;
            p1warn = warn;
        } else {
            p2vpCounter = vpCounter;
            p2doneButton = doneButton;
            p2warn = warn;
        }

        return panel;
    }

    private void warn(Player player, String s)
    {
        var lb = player == Player.PLAYER1 ? p1warn : p2warn;
        lb.setText(s);
        lb.repaint();
        if (currWarnThread != null) {
            currWarnThread.interrupt();
        }
        currWarnThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                lb.setText(" ");
                lb.repaint();
            } catch (InterruptedException ignored) {}
        });
        currWarnThread.start();
    }

    private void refresh()
    {
        var p1model = con.getPlayerMachineSelection(Player.PLAYER1);
        p1vpCounter.setText(String.format("VP: %d/%d", p1model.currentVictoryPoints(), p1model.maxVictoryPoints()));
        var p2model = con.getPlayerMachineSelection(Player.PLAYER2);
        p2vpCounter.setText(String.format("VP: %d/%d", p2model.currentVictoryPoints(), p2model.maxVictoryPoints()));
        p1container.repaint();
        p2container.repaint();
    }

    public void show(Player firstPlayer)
    {
        var grid = firstPlayer == Player.PLAYER1 ? p1gridModel : p2gridModel;
        stats = new MachineStatsPanel(grid.machineUnderCursor());
        frame.getContentPane().add(stats, BorderLayout.WEST);
        frame.pack();
        frame.setVisible(true);
        // setFocused after setVisible, otherwise requestFocus will fail
        var panel = firstPlayer == Player.PLAYER1 ? p1machSelPanel : p2machSelPanel;
        panel.setFocused(true);
    }

    @Override
    public void selectionFinished(MachineInventory p1inventory, MachineInventory p2inventory, IPiecePlacementController nextCon)
    {
        var nextView = new PiecePlacementView(nextCon);
        nextView.show();
        frame.dispose();
    }

    @Override
    public void acceptSelectionResponse(Player player, Machine machine, MachineSelectionResponse response)
    {
        var doneButton = player == Player.PLAYER1 ? p1doneButton : p2doneButton;
        if (response == MachineSelectionResponse.SELECTED) {
            doneButton.setEnabled(true);
        } else if (response == MachineSelectionResponse.UNAVAILABLE_VICTORY_POINTS) {
            warn(player, "You don't have any more victory points to spare for this piece!");
        } else if (response == MachineSelectionResponse.UNAVAILABLE_AMOUNT) {
            warn(player, "You don't have any more pieces of this type!");
        }
        refresh();
    }

    @Override
    public void switchToPlayer(Player player)
    {
        var prevDoneButton = player == Player.PLAYER1 ? p2doneButton : p1doneButton;
        prevDoneButton.setEnabled(false);
        var panel = player == Player.PLAYER1 ? p1machSelPanel : p2machSelPanel;
        var other = panel == p1machSelPanel ? p2machSelPanel : p1machSelPanel;
        panel.setFocused(true);
        other.setFocused(false);
        refresh();
    }

    @Override
    public void acceptDeselectionResponse(Player player, Machine machine, MachineDeselectionResponse response)
    {
        if (response == MachineDeselectionResponse.DESELECTED) {
            var machSel = con.getPlayerMachineSelection(player);
            if (machSel.isSelectionEmpty()) {
                (player == Player.PLAYER1 ? p1doneButton : p2doneButton).setEnabled(false);
            }
        } else if (response == MachineDeselectionResponse.NONE_SELECTED) {
            warn(player, "You're de-selecting a piece you haven't even selected!");
        }
        refresh();
    }
}
