package components;

import logic.Player;

import java.awt.*;

public class Palette
{
    public static Color p1color = new Color(32, 100, 145);
    public static Color p2color = new Color(145, 32, 100);

    public static Color p1colorTransparent = new Color(32, 100, 145, (int) (0.4f * 255));
    public static Color p2colorTransparent = new Color(145, 32, 100, (int) (0.4f * 255));

    public static Color red = new Color(240, 100, 100);
    public static Color darkRed = Palette.red.darker().darker().darker();
    public static Color transparentRed = new Color(240, 100, 100, 100);

    public static Color color(Player p) {
        return p == Player.PLAYER1 ? p1color : p2color;
    }

    public static Color transparentColor(Player p) {
        return p == Player.PLAYER1 ? p1colorTransparent : p2colorTransparent;
    }

    public static Color transparentYellow = new Color(0.6f, 1f, 1f, 0.5f);
    public static Color transparentBlack = new Color(0, 0, 0, 0.2f);

    public static Color yellow = Color.YELLOW;
    public static Color darkYellow = yellow.darker().darker().darker();

    public static Color green = new Color(131, 252, 184);
    public static Color darkGreen = green.darker().darker().darker();

    public static Color armoredPt = new Color(91, 144, 212);
    public static Color weakPt = new Color(192, 71, 71);

    public static Color pink = new Color(255, 190, 220);
    public static Color darkPink = pink.darker().darker().darker();
}
