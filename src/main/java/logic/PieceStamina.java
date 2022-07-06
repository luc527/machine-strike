package logic;

public class PieceStamina implements IPIeceStamina
{
    private static final int MAXK = 3;

    private int k = 0;

    public boolean canWalk() { return k+1 <= MAXK; }

    public boolean canRun() { return k+2 <= MAXK; }

    public boolean canAttack() { return k+1 <= MAXK; }

    public boolean walkWouldOvercharge() { return k+1 == MAXK; }

    public boolean runWouldOvercharge() { return k+2 == MAXK; }

    public boolean attackWouldOvercharge() { return k+1 == MAXK; }

    public boolean walkAndAttackWouldOvercharge() { return k+2 == MAXK; }

    public void walk()
    {
        if (!canWalk()) throw new RuntimeException("PieceStamina: Cannot walk!");
        k += 1;
    }

    public void run()
    {
        if (!canRun()) throw new RuntimeException("PieceStamina: Cannot run!");
        k += 2;
    }

    public void attack()
    {
        if (!canAttack()) throw new RuntimeException("PieceStamina: Cannot attack!");
        k += 1;
    }

    public boolean overcharged() { return k == MAXK; }

    public void reset() { k = 0; }

    @Override
    public String toString()
    {
        return "PieceStamina{" +
            "k=" + k +
            '}';
    }
}
