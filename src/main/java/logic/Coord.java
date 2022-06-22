package logic;

import constants.Constants;

// :PatternUsed Factory (kind of)
// Both Java and Python do a similar thing for int object wrappers,
// caching them up to 128 or something

public class Coord
{
    private final int row;
    private final int col;

    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final Coord[][] cache;

    static {
        cache = new Coord[ROWS][];
        for (int row = 0; row < ROWS; row++) {
            cache[row] = new Coord[COLS];
            for (int col = 0; col < COLS; col++)
                cache[row][col] = new Coord(row, col);
        }
    }

    public static Coord create(int row, int col)
    {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS)
            return cache[row][col];
        return new Coord(row, col);
    }

    public static Coord create(Coord cursor)
    {
        return Coord.create(cursor.row(), cursor.col());
    }

    public int row() { return row; }
    public int col() { return col; }

    private Coord(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public Coord withRow(int row)
    {
        return Coord.create(row, this.col);
    }

    public Coord withCol(int col)
    {
        return Coord.create(this.row, col);
    }

    public Coord moved(Direction dir, int maxRow, int maxCol) {
        var row = row();
        var col = col();
        switch (dir) {
            case WEST -> col--;
            case EAST -> col++;
            case NORTH -> row--;
            case SOUTH -> row++;
        }
        return (row < 0 || row > maxRow || col < 0 || col > maxCol) ? null : Coord.create(row, col);
    }
}
