package graphics;

import logic.Player;

import java.awt.*;

public class Palette
{
    public static Color p1color = new Color(32, 100, 145);
    public static Color p2color = new Color(145, 32, 100);

    public static Color color(Player p) {
        return p == Player.PLAYER1 ? p1color : p2color;
    }


    public static Color transparentBlack = new Color(0, 0, 0, 0.2f);

    public static Color yellow = Color.YELLOW;
    public static Color darkYellow = yellow.darker().darker().darker();

    public static Color green = new Color(131, 252, 184);
    public static Color darkGreen = green.darker().darker().darker();
}
