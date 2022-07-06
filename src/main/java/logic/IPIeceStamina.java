package logic;

public interface IPIeceStamina
{
    boolean canWalk();

    boolean canRun();

    boolean canAttack();

    boolean walkWouldOvercharge();

    boolean runWouldOvercharge();

    boolean attackWouldOvercharge();

    boolean walkAndAttackWouldOvercharge();

    String toString();
}
