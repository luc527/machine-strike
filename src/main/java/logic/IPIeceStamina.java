package logic;

public interface IPIeceStamina
{
    boolean canWalk();

    boolean canRun();

    boolean canAttack();

    boolean canWalkAndAttack();

    boolean walkWouldOvercharge();

    boolean runWouldOvercharge();

    boolean attackWouldOvercharge();

    boolean walkAndAttackWouldOvercharge();

    String toString();
}
