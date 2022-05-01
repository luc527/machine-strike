package logic;

import java.util.List;

public enum Direction
{
    EAST  (0, 3*Math.PI/2),
    NORTH (1, 0.0),
    WEST  (2, Math.PI/2),
    SOUTH (3, Math.PI);

    private final int idx;
    private final double theta;

    Direction(int idx, double theta)
    {
        this.idx = idx;
        this.theta = theta;
    }

    public int idx()
    {
        return this.idx;
    }

    public double theta()
    {
        return this.theta;
    }

    public static Direction from(int idx)
    {
        switch (idx) {
            case 0: return EAST;
            case 1: return NORTH;
            case 2: return WEST;
            case 3: return SOUTH;
        }
        throw new RuntimeException("Invalid direction index: " + idx);
    }

    /**
     * @param times How many times to rotate; positive for counterclockwise, negative for clockwise.
     * @return Direction
     */
    public Direction rotated(int times)
    {
        // https://stackoverflow.com/a/39740009
        var i = this.idx;
        i += times;
        i += (1 - i / 4) * 4;
        return Direction.from(i % 4);
    }

    public static Iterable<Direction> iter()
    {
        return List.of(Direction.values());
    }
}
