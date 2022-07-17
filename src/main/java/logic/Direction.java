package logic;

import java.util.List;

public enum Direction
{
    EAST  (0, Math.PI/2),
    NORTH (1, 0.0),
    WEST  (2, 3*Math.PI/2),
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

    public Direction cycle(boolean right)
    {
        if (right) {
            switch (this) {
                case NORTH -> { return EAST; }
                case EAST -> { return SOUTH; }
                case SOUTH -> { return WEST; }
                case WEST -> { return NORTH; }
            }
        } else {
            switch (this) {
                case NORTH -> { return WEST; }
                case WEST -> { return SOUTH; }
                case SOUTH -> { return EAST; }
                case EAST -> { return NORTH; }
            }
        }
        throw new RuntimeException("Reached unreachable code!");
    }

    public Direction opposite() {
        switch (this) {
            case NORTH -> { return SOUTH; }
            case SOUTH -> { return NORTH; }
            case EAST -> { return WEST; }
            case WEST -> { return EAST; }
        }
        throw new RuntimeException("Reached unreachable code!");
    }

    public static Iterable<Direction> all()
    {
        return List.of(Direction.values());
    }
}
