package logic;

import logic.turn.PieceTurn;

public interface IPieceTurn
{
    boolean canWalk();

    boolean walkWouldOvercharge();

    boolean canRun();

    boolean runWouldOvercharge();
}
