package logic;

import utils.Constants;

// :PatternUsed Factory (kind of)
// Both Java and Python do a similar thing for int object wrappers,
// caching them up to 128 or something. Observe that we can't do
// the same thing for the whole CoordState, since it is mutable.

// TODO instead make Coord immutable, but with cache!

public class CoordCache
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final ICoord[][] cache;

    static {
        cache = new CoordState[ROWS][];
        for (var row = 0; row < ROWS; row++) {
            cache[row] = new CoordState[COLS];
            for (var col = 0; col < COLS; col++) {
                cache[row][col] = new CoordState(row, col);
            }
        }
    }

    public static ICoord get(int row, int col)
    {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return new CoordState(row, col);
        } else {
            return cache[row][col];
        }
    }

}
