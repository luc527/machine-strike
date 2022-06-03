package gameplay;

import components.Palette;
import components.boardgrid.BoardGridPanel;

import java.awt.*;

public class GameGridPanel extends BoardGridPanel
{
    public GameGridPanel(GameGridModel grid)
    {
        super(grid);
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        super.panelPaintComponent(G);

        var g = (Graphics2D) G;
        super.paintTerrain(g);

        var grid = (GameGridModel) this.grid;

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var piece = grid.pieceAt(row, col);
                if (piece == null) continue;
                var x = SIDE_PX * col;
                var y = SIDE_PX * row;
                super.drawMachine(piece.machine(), piece.direction(), g, x, y);
                g.setColor(Palette.transparentColor(piece.player()));
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }
        }

        super.paintAvailablePositions(g);

        if (grid.isCarryingPiece()) {
            var carried = grid.getCarriedPiece();
            var direction = grid.getCarriedPieceDirection();
            var cy = SIDE_PX * grid.getCursor().row();
            var cx = SIDE_PX * grid.getCursor().col();
            super.drawMachine(carried.machine(), direction, g, cx, cy);
        }

        super.paintCursor(g);
    }
}
