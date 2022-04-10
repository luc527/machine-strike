package logic;

public enum Direction
{
    EAST(0),
    NORTH(1),
    WEST(2),
    SOUTH(3);

    private int idx;

    Direction(int idx) {
        this.idx = idx;
    }

    public int idx() {
        return this.idx;
    }

    public static Direction from(int idx) {
        switch (idx) {
            case 0: return EAST;
            case 1: return NORTH;
            case 2: return WEST;
            case 3: return SOUTH;
        }
        throw new RuntimeException("Invalid direction index: " + idx);
    }

    // positive for left, negative for right
    public Direction rotate(int times)
    {
        // https://stackoverflow.com/a/39740009
        var i = this.idx;
        i += times;
        i += (1 - i / 4) * 4;
        return Direction.from(i % 4);
    }
}
