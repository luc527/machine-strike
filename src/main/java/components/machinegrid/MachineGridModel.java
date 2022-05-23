package components.machinegrid;

import logic.Coord;
import logic.Direction;
import logic.Machine;

import java.util.List;

/**
 * Encapsulates a grid of machines wherein you can move a cursor.
 * Supports arbitrary combinations of machines per row  x  number of machines
 * (i.e. the grid doesn't need to be rectangular, like 9 machines, 3 per row,
 * it supports something like 8 machines, 3 per row, where the last row has a
 * blank square).
 */
public class MachineGridModel
{
    private final int machinesPerRow;
    private final Machine[][] machines;
    private final int nmachines;
    private final int rows;
    private Coord cursor;

    public MachineGridModel(List<Machine> machines, int machinesPerRow)
    {
        this.machinesPerRow = machinesPerRow;
        cursor = Coord.create(0, 0);

        nmachines = machines.size();
        rows = (nmachines - 1) / machinesPerRow + 1; // n/MACHINES rounded up
        this.machines = new Machine[rows][];
        for (var row = 0; row < rows; row++) {
            this.machines[row] = new Machine[machinesPerRow];
        }

        var i = 0;
        for (var machine : machines) {
            var row = i / machinesPerRow;
            var col = i % machinesPerRow;
            this.machines[row][col] = machine;
            i++;
        }
    }

    public int rows()
    {
        return rows;
    }

    public int cols()
    {
        return machinesPerRow;
    }

    public Coord cursor()
    {
        return cursor;
    }

    private int clamp(int x, int min, int max)
    {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public void moveCursor(Direction dir)
    {
        if (dir == Direction.WEST || dir == Direction.EAST) {
            var offset = dir == Direction.WEST ? -1 : 1;
            var lastCol = nmachines % machinesPerRow > 0 && cursor.row() == rows - 1 ? nmachines % machinesPerRow - 1 : machinesPerRow - 1;
            cursor = cursor.withCol(clamp(cursor.col() + offset, 0, lastCol));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            var lastRow = nmachines % machinesPerRow > 0 && cursor.col() >= nmachines % machinesPerRow ? rows - 2 : rows - 1;
            cursor = cursor.withRow(clamp(cursor.row() + offset, 0, lastRow));
        }
    }

    public Machine machineAt(Coord coord)
    {
        return machines[coord.row()][coord.col()];
    }

    public Machine machineUnderCursor()
    {
        return machineAt(cursor());
    }

    public interface MachineGridIterator
    {
        void exec(int row, int col);
    }

    public void iterate(MachineGridIterator it)
    {
        for (var i = 0; i < nmachines; i++) {
            var row = i / machinesPerRow;
            var col = i % machinesPerRow;
            it.exec(row, col);
        }
    }
}
