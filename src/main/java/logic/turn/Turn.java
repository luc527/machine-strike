package logic.turn;

public class Turn implements ITurn
{
    private int k = 0;

    public boolean overcharged() { return k > 2; }

    public boolean canWalk() { return k <= 2; }

    public boolean walkWouldOvercharge() { return k == 2; }

    public boolean canRun() { return k <= 1; }

    public boolean runWouldOvercharge() { return k == 1; }

    public void walk()
    {
        var knext = k + 1;
        if (knext > 3)
            throw new UnsupportedOperationException("Exhausted player moves");
        k = knext;
    }

    public void run()
    {
        var knext = k + 2;
        if (knext > 3)
            throw new UnsupportedOperationException("Exhausted player moves");
        k = knext;
    }

    public void reset()
    { k = 0; }

    public String toString() { return String.format("Turn{ k = %d/3 }", k); }
}
