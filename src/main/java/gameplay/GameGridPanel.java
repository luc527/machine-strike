package gameplay;

import components.Palette;
import logic.GameLogic;
import logic.Utils;
import components.boardgrid.BoardGridPanel;

import java.awt.*;

public class GameGridPanel extends BoardGridPanel
{
    public GameGridPanel(GameGridModel grid)
    {
        super(grid);
    }

    private void drawHealth(Graphics2D g, int row, int col, int health) {
        var x = SIDE_PX * col + 2;
        var y = SIDE_PX * row + SIDE_PX - 2;
        Utils.drawOutlinedString(g, x, y, ""+health, Palette.darkPink, Palette.pink);
    }

    private void drawDamage(Graphics2D g, int row, int col, int dmg) {
        var x = SIDE_PX * col + SIDE_PX / 2 - 5;
        var y = SIDE_PX * row + SIDE_PX / 2 + 10;
        Utils.drawOutlinedString(g, x, y, "-"+dmg, Palette.darkPink, Palette.pink);
    }

    private void drawCombatPower(Graphics2D g, int row, int col, int pow) {
        var x = SIDE_PX * col + SIDE_PX - 5;
        var y = SIDE_PX * row + SIDE_PX - 2;
        Utils.drawOutlinedString(g, x, y, ""+pow, Palette.darkGreen, Palette.green);
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        super.panelPaintComponent(G);

        var g = (Graphics2D) G;
        super.paintTerrain(g);

        var grid = (GameGridModel) this.grid;

        super.paintAvailablePositions(g);

        super.paintCursor(g);

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var piece = grid.pieceAt(row, col);
                if (piece == null) continue;
                var x = SIDE_PX * col;
                var y = SIDE_PX * row;
                g.setColor(Palette.transparentColor(piece.player()));
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
                super.drawMachine(piece.machine(), piece.direction(), g, x, y);
                drawHealth(g, row, col, piece.health());
            }
        }

        if (grid.isCarryingPiece()) {
            var piece = grid.getCarriedPiece();
            var direction = grid.getCarriedPieceDirection();
            var row = grid.getCursor().row();
            var col = grid.getCursor().col();
            var cy = SIDE_PX * row;
            var cx = SIDE_PX * col;
            super.drawMachine(piece.machine(), direction, g, cx, cy);
            drawHealth(g, row, col, piece.health());

            if (grid.isCarriedPieceAttacking()) {
                var attackedPiece = grid.getAttackedPiece();
                var attackedCoord = grid.getAttackedCoord();
                var defRow = attackedCoord.row();
                var defCol = attackedCoord.col();

                var terrains = grid.getBoard();

                var atkCombatPower = GameLogic.combatPower(piece.machine(), terrains.get(row, col), direction);
                drawCombatPower(g, row, col, atkCombatPower);

                var dmg = GameLogic.conflictDamage(
                        piece.machine(), direction, terrains.get(row, col),
                        attackedPiece.machine(), terrains.get(defRow, defCol)
                );

                if (dmg.atkDamage != 0) drawDamage(g,    row,    col, dmg.atkDamage);
                if (dmg.defDamage != 0) drawDamage(g, defRow, defCol, dmg.defDamage);
            } else {
                var combatPower = GameLogic.combatPower(piece.machine(), grid.getBoard().get(row, col));
                drawCombatPower(g, row, col, combatPower);
            }
        }

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var x = col * SIDE_PX;
                var y = row * SIDE_PX;
                var piece = grid.pieceAt(row, col);
                if (piece == null) continue;
                var combatPower = GameLogic.combatPower(piece.machine(), grid.getBoard().get(row, col));
                drawCombatPower(g, x, y, combatPower);
            }
        }
    }
}
