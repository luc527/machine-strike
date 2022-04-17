package logic;

import java.awt.*;

public enum Player
{
    PLAYER1(new Color(32, 145, 100)),
    PLAYER2(new Color(145, 32, 100));

    private final Color color;

    Player(Color color)
    {
        this.color = color;
    }

    public Color color()
    {
        return this.color;
    }

    public Player next()
    {
        return this == PLAYER1 ? PLAYER2 : PLAYER1;
    }
}
