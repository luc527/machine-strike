package gameplay;

import components.Palette;
import logic.Utils;
import components.boardgrid.BoardGridPanel;
import logic.GameState;

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
            if (grid.isCarriedPieceAttacking()) {
                var attackedPiece = grid.getAttackedPiece();
                var attackedCoord = grid.getAttackedCoord();
                var arow = attackedCoord.row();
                var acol = attackedCoord.col();

                var terrains = grid.getBoard();
                var dmg = GameState.conflictDamage(piece, attackedPiece, terrains.get(row, col), terrains.get(arow, acol));

                System.out.println("Attacked dmg: " + dmg.attackedDmg);
                System.out.println("Attacker dmg: " + dmg.attackerDmg);

                var ay = SIDE_PX * arow;
                var ax = SIDE_PX * acol;
            } else {
                var combatPower = GameState.combatPower(piece.machine(), grid.getBoard().get(row, col));
                Utils.drawOutlinedString(g, cx + 2, cy + SIDE_PX - 2, "" + combatPower, Color.BLACK, Color.WHITE);
            }
        }

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var x = col * SIDE_PX;
                var y = row * SIDE_PX;
                var piece = grid.pieceAt(row, col);
                if (piece == null) continue;
                var combatPower = GameState.combatPower(piece.machine(), grid.getBoard().get(row, col));
                Utils.drawOutlinedString(g, x + 2, y + SIDE_PX - 2, "" + combatPower, Color.BLACK, Color.WHITE);
            }
        }
    }
}
