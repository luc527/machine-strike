package logic;

public enum Reachability
{
    IN,
    IN_RUNNING,
    OUT;

    public boolean in() { return this == IN; }
    public boolean inRunning() { return this == IN_RUNNING; }
    public boolean out() { return this == OUT; }
}
