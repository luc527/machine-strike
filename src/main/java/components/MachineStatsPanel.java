package components;

import assets.MachineImageMap;
import logic.Direction;
import logic.Machine;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MachineStatsPanel extends JPanel
{
    private final JLabel machineImage;
    private final JLabel name;
    private final JLabel attackPower;
    private final JLabel attackRange;
    private final JLabel movementRange;
    private final JLabel health;
    private final JLabel victoryPoints;
    private final JLabel points;

    public MachineStatsPanel(Machine initialMachine)
    {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(machineImage = new JLabel());
        add(name = new JLabel());
        add(attackPower = new JLabel());
        add(attackRange = new JLabel());
        add(movementRange = new JLabel());
        add(health = new JLabel());
        add(victoryPoints = new JLabel());
        add(points = new JLabel());
        setMachine(initialMachine);
    }

    public JPanel setMachine(Machine machine)
    {
        machineImage.setIcon(new ImageIcon(MachineImageMap.get(machine)));
        name.setText("<html><b>Name:</b> "+machine.name()+"</html>");
        attackPower.setText("<html><b>Atk power:</b> "+machine.attackPower()+"</html>");
        attackRange.setText("<html><b>Atk range:</b> "+machine.attackRange()+"</html>");
        movementRange.setText("<html><b>Mov range:</b> "+machine.movementRange()+"</html>");
        health.setText("<html><b>Health:</b> "+machine.health()+"</html>");
        victoryPoints.setText("<html><b>VP:</b> "+machine.victoryPoints()+"</html>");

        var armored = new ArrayList<String>();
        var weak    = new ArrayList<String>();
        var neutral = new ArrayList<String>();
        for (var dir : Direction.iter()) {
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
