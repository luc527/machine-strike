package logic;

public enum Player
{
    PLAYER1,
    PLAYER2;

    public Player next()
    {
        return this == PLAYER1 ? PLAYER2 : PLAYER1;
    }
}
