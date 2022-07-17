package components;

import assets.MachineImageMap;
import logic.Direction;
import logic.Machine;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MachineStatsPanel extends JPanel
{
    private final JLabel machineImage;
    private final JLabel name;
    private final JLabel type;
    private final JLabel attackPower;
    private final JLabel attackRange;
    private final JLabel movementRange;
    private final JLabel health;
    private final JLabel victoryPoints;
    private final JLabel points;

    public MachineStatsPanel()
    {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(machineImage = new JLabel());
        add(name = new JLabel());
        add(type = new JLabel());
        add(attackPower = new JLabel());
        add(attackRange = new JLabel());
        add(movementRange = new JLabel());
        add(health = new JLabel());
        add(victoryPoints = new JLabel());
        add(points = new JLabel());
    }

    public MachineStatsPanel(Machine initialMachine)
    {
        this();
        setMachine(initialMachine);
    }

    @Override
    public Dimension getPreferredSize()
    {
        var dim = super.getPreferredSize();
        return new Dimension(200, dim.height);
    }

    private void toggle(boolean b)
    {
        var comps = new JComponent[]{machineImage, name, type, attackPower, attackRange, movementRange, health, victoryPoints, points};
        for (var comp : comps) comp.setVisible(b);
    }

    public JPanel setMachine(Machine machine)
    {
        if (machine == null) {
            toggle(false);
            return this;
        }
        toggle(true);

        machineImage.setIcon(new ImageIcon(MachineImageMap.get(machine)));
        name.setText("<html><b>Name:</b> "+machine.name()+"</html>");
        type.setText("<html><b>Type:</b> "+machine.type().name()+"</html>");
        attackPower.setText("<html><b>Atk power:</b> "+machine.attackPower()+"</html>");
        attackRange.setText("<html><b>Atk range:</b> "+machine.attackRange()+"</html>");
        movementRange.setText("<html><b>Mov range:</b> "+machine.movementRange()+"</html>");
        health.setText("<html><b>Health:</b> "+machine.health()+"</html>");
        victoryPoints.setText("<html><b>VP:</b> "+machine.victoryPoints()+"</html>");

        var armored = new ArrayList<String>();
        var weak    = new ArrayList<String>();
        var neutral = new ArrayList<String>();
        for (var dir : Direction.all()) {
            var pt = machine.point(dir);
            switch (pt) {
                case ARMORED -> armored.add(dir.toString());
                case WEAK    ->    weak.add(dir.toString());
                case EMPTY   -> neutral.add(dir.toString());
            }
        }
        var armoredString = armored.isEmpty() ? "" : String.format("<br><u>Armored:</u> %s</li>", String.join(", ", armored));
        var    weakString =    weak.isEmpty() ? "" : String.format("<br><u>Weak:</u> %s</li>", String.join(", ", weak));
        var neutralString = neutral.isEmpty() ? "" : String.format("<br><u>Neutral:</u> %s</li>", String.join(", ", neutral));

        var pointsString = String.join("", Arrays.asList(armoredString, weakString, neutralString));

        points.setText("<html><b>Points:</b> "+pointsString+"</html>");

        return this;
    }
}
