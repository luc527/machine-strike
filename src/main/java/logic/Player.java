package logic;

public enum Player
{
    PLAYER1,
    PLAYER2;

    public Player next()
    {
        if (this == PLAYER1) return PLAYER2;
        return PLAYER1;
    }
}