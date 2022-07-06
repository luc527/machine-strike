package logic;

public interface IPiece
{
    Machine machine();

    Direction direction();

    int health();

    Player player();

    boolean dead();

    IPIeceStamina stamina();
}
