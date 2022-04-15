package logic;

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
}
