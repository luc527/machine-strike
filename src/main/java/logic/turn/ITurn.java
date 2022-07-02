package logic.turn;

public interface ITurn
{
    boolean overcharged();

    boolean canWalk();

    boolean walkWouldOvercharge();

    boolean canRun();

    boolean runWouldOvercharge();
}
