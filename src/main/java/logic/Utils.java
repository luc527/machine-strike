package logic;

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

}
