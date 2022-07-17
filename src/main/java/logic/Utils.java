package logic;

import assets.MachineImageMap;
import components.Palette;
import constants.Constants;

import java.awt.*;

public class Utils
{
    public static void drawOutlinedString(Graphics2D g, int x, int y, String text)
    {
        drawOutlinedString(g, x, y, text, Color.BLACK, Color.WHITE);
    }

    public static void drawOutlinedString(Graphics2D g, int x, int y, String text, Color background, Color foreground)
    {
        g.setColor(background);
        for (var yoff = -1; yoff <= 1; yoff++)
            for (var xoff = -1; xoff <= 1; xoff++)
                g.drawString(text, x + xoff, y + yoff);
        g.setColor(foreground);
        g.drawString(text, x, y);
    }

    public static int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public static void drawMachine(Machine machine, Direction direction, Graphics2D g, int x, int y)
    {
        final var SIDE_PX = Constants.BOARD_SIDE_PX;

        var img = MachineImageMap.get(machine);
        var xformSaved = g.getTransform();
        g.rotate(direction.theta(), x + SIDE_PX / 2.0, y + SIDE_PX / 2.0);

        for (var dir : Direction.all()) {
            var pt = machine.point(dir);
            if (pt == Machine.Point.EMPTY) continue;
            var xformSaved2 = g.getTransform();
            g.rotate(dir.theta(), x + SIDE_PX / 2.0, y + SIDE_PX / 2.0);
            var color = pt == Machine.Point.ARMORED ? Palette.armoredPt : Palette.weakPt;
            g.setColor(color);
            g.fillRect(x+11, y+1, SIDE_PX-22, 10);
            g.setTransform(xformSaved2);
        }
        g.drawImage(img, x, y, null);

        g.setTransform(xformSaved);
    }

}
