package logic;

import java.util.Objects;

public class Coord
{
    private int row;
    private int col;

    public Coord(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int row()
    {
        return row;
    }

    public int col()
    {
        return col;
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public void setCol(int col)
    {
        this.col = col;
    }

    public void set(Coord coord)
    {
        this.row = coord.row;
        this.col = coord.col;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return row == coord.row && col == coord.col;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(row, col);
    }
}
