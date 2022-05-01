package components.machinegrid;

import logic.Coord;
import logic.Direction;
import logic.Machine;

import java.util.List;

public class MachineGridModel
{
    private final int machinesPerRow;
    private final Machine[][] machines;
    private final int n;
    private final int rows;
    private Coord cursor;

    public MachineGridModel(List<Machine> machines, int machinesPerRow)
    {
        this.machinesPerRow = machinesPerRow;
        cursor = Coord.create(0, 0);

        n = machines.size();
        rows = (n - 1) / machinesPerRow + 1; // n/MACHINES rounded up
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
            var lastCol = n % machinesPerRow > 0 && cursor.row() == rows - 1 ? n % machinesPerRow - 1 : machinesPerRow - 1;
            cursor = cursor.withCol(clamp(cursor.col() + offset, 0, lastCol));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            var lastRow = n % machinesPerRow > 0 && cursor.col() >= n % machinesPerRow ? rows - 2 : rows - 1;
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
        for (var i = 0; i < n; i++) {
            var row = i / machinesPerRow;
            var col = i % machinesPerRow;
            it.exec(row, col);
        }
    }
}
