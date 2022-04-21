package logic;

import utils.Constants;

import java.util.Objects;

public class CoordState implements ICoord
{
    private int row;
    private int col;

    public CoordState(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public int row()
    {
        return row;
    }

    @Override
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

    public void set(ICoord coord)
    {
        this.row = coord.row();
        this.col = coord.col();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordState coord = (CoordState) o;
        return row == coord.row && col == coord.col;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(row, col);
    }
}
