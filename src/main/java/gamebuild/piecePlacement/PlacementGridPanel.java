package gamebuild.piecePlacement;


import components.Palette;
import components.boardgrid.BoardGridPanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class PlacementGridPanel extends BoardGridPanel
{
    private Runnable onConfirm = () -> {};
    private Runnable onCancel = () -> {};

    public PlacementGridPanel(PlacementGridModel grid)
    {
        super(grid);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                var c = e.getKeyChar();
                var k = e.getKeyCode();
                if      (c == 'q')                    grid.rotateCarriedMachine(false);
                else if (c == 'e')                    grid.rotateCarriedMachine(true);
                else if (k == KeyEvent.VK_ENTER)      onConfirm.run();
                else if (k == KeyEvent.VK_BACK_SPACE
                      || k == KeyEvent.VK_ESCAPE)     onCancel.run();
                repaint();
            }
        });
    }

    public void onConfirm(Runnable r)
    { onConfirm = Objects.requireNonNullElse(r, () -> {}); }

    public void onCancel(Runnable r)
    { onCancel = Objects.requireNonNullElse(r, () -> {}); }

    @Override
    public void paintComponent(Graphics G)
    {
        panelPaintComponent(G);
        var g = (Graphics2D) G;

        super.paintTerrain(g);

        var grid = (PlacementGridModel) this.grid;

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var piece = grid.pieceAt(row, col);
                if (piece == null) continue;
                var x = col * SIDE_PX;
                var y = row * SIDE_PX;
                super.drawMachine(piece.machine(), piece.direction(), g, x, y);
                g.setColor(Palette.transparentColor(piece.player()));
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }
        }

        super.paintAvailablePositions(g);

        var cursor = grid.getCursor();
        var x = SIDE_PX * cursor.col();
        var y = SIDE_PX * cursor.row();
        if (super.showCursor && grid.isCarryingMachine()) {
            super.drawMachine(
                grid.getCarriedMachine(),
                grid.getCarriedMachineDirection(),
                g, x, y
            );
        }

        super.paintCursor(g);
    }
}
