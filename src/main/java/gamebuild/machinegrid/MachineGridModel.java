package gamebuild.machinegrid;

import gamebuild.IMachineInventory;
import logic.CoordState;
import logic.Direction;
import logic.ICoord;

import java.util.Optional;

public class MachineGridModel
{
    private static final int MACHINES_PER_ROW = 4;

    private final IMachineInventory inv;
    private final String[][] machines;
    private final int n;
    private final int rows;
    private final CoordState cursor;

    public MachineGridModel(IMachineInventory inv)
    {
        this.inv = inv;
        cursor = new CoordState(0, 0);

        var machines = inv.getMachines();
        n = machines.size();
        rows = (n - 1) / MACHINES_PER_ROW + 1; // n/MACHINES rounded up
        this.machines = new String[rows][];
        for (var row = 0; row < rows; row++) {
            this.machines[row] = new String[MACHINES_PER_ROW];
        }

        var i = 0;
        for (var machine : machines) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
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
        return MACHINES_PER_ROW;
    }

    public ICoord cursor()
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
            var lastCol = cursor.row() == rows - 1 ? n % MACHINES_PER_ROW - 1 : MACHINES_PER_ROW - 1;
            cursor.setCol(clamp(cursor.col() + offset, 0, lastCol));
        } else {
            var offset = dir == Direction.NORTH ? -1 : 1;
            var lastRow = cursor.col() >= n % MACHINES_PER_ROW ? rows - 2 : rows - 1;
            cursor.setRow(clamp(cursor.row() + offset, 0, lastRow));
        }
    }

    public String machineUnderCursor()
    {
        return machines[cursor.row()][cursor.col()];
    }

    public interface MachineGridIterator
    {
        void exec(int row, int col, String machine, int amount);
    }

    public void iterate(MachineGridIterator it)
    {
        for (var i = 0; i < n; i++) {
            var row = i / MACHINES_PER_ROW;
            var col = i % MACHINES_PER_ROW;
            var machine = machines[row][col];
            it.exec(row, col, machine, inv.getAmount(machine));
        }
    }
}
