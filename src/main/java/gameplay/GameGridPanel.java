package gameplay;

import components.Palette;
import logic.Coord;
import logic.GameLogic;
import logic.Reachability;
import logic.Utils;
import components.boardgrid.BoardGridPanel;

import java.awt.*;

public class GameGridPanel extends BoardGridPanel
{
    public GameGridPanel(GameGridModel grid)
    {
        super(grid);
    }

    private void drawHealth(Graphics2D g, int row, int col, int health)
    {
        var x = SIDE_PX * col + 2;
        var y = SIDE_PX * row + SIDE_PX - 2;
        Utils.drawOutlinedString(g, x, y, ""+health, Palette.darkPink, Palette.pink);
    }

    private void drawDamage(Graphics2D g, int row, int col, int dmg)
    {
        var x = SIDE_PX * col + SIDE_PX / 2 - 5;
        var y = SIDE_PX * row + SIDE_PX / 2 + 10;
        Utils.drawOutlinedString(g, x, y, "-"+dmg, Palette.darkPink, Palette.pink);
    }

    private void drawCombatPower(Graphics2D g, int row, int col, int pow)
    {
        var x = SIDE_PX * col + SIDE_PX - 5;
        var y = SIDE_PX * row + SIDE_PX - 2;
        Utils.drawOutlinedString(g, x, y, ""+pow, Palette.darkGreen, Palette.green);
    }

    private void drawOverchargeWarning(Graphics2D g, int row, int col)
    {
        var x = SIDE_PX * col + 7;
        var y = SIDE_PX * row + 17;
        Utils.drawOutlinedString(g, x, y, "OV!", Palette.darkYellow, Palette.yellow);
    }

    @Override
    public void paintAvailablePositions(Graphics2D g)
    {
        var grid = (GameGridModel) this.grid;
        if (!grid.isCarryingPiece()) return;

        var savedColor = g.getColor();

        var inColor = Palette.transparentYellow;
        var inRunningColor = Palette.transparentYellow.darker().darker();

        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var reach = grid.isReachable(row, col);
                if (reach == Reachability.OUT) continue;
                var color = reach == Reachability.IN ? inColor : inRunningColor;
                g.setColor(color);
                var x = col * SIDE_PX;
                var y = row * SIDE_PX;
                g.fillRect(x, y, SIDE_PX, SIDE_PX);
            }
        }

        g.setColor(savedColor);
    }

    @Override
    protected void paintComponent(Graphics G)
    {
        panelPaintComponent(G);

        var g = (Graphics2D) G;
        paintTerrain(g);

        var grid = (GameGridModel) this.grid;

        paintAvailablePositions(g);

        paintCursor(g);

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
            var cursor = grid.getCursor();
            var row = cursor.row();
            var col = cursor.col();
            var cy = SIDE_PX * row;
            var cx = SIDE_PX * col;
            super.drawMachine(piece.machine(), direction, g, cx, cy);

            var health = 0;
            var reach = grid.isReachable(row, col);
            var overcharge = (reach.in() && piece.turn().walkWouldOvercharge())
                          || (reach.inRunning() && piece.turn().runWouldOvercharge());
            if (overcharge) {
                drawOverchargeWarning(g, row, col);
                drawDamage(g, row, col, GameLogic.OVERCHARGE_DAMAGE);
                health = piece.health() - GameLogic.OVERCHARGE_DAMAGE;
            } else {
                health = piece.health();
            }
            drawHealth(g, row, col, health);

            if (grid.isCarriedPieceAttacking()) {
                var attackedPiece = grid.getAttackedPiece();
                var attackedCoord = grid.getAttackedCoord();
                var defRow = attackedCoord.row();
                var defCol = attackedCoord.col();

                var terrains = grid.getBoard();

                var atkCombatPower = GameLogic.combatPower(piece.machine(), terrains.get(row, col), direction);
                drawCombatPower(g, row, col, atkCombatPower);

                var conflict = grid.getConflictResult(cursor, attackedCoord, piece, attackedPiece, direction);

                if (conflict.atkDamage() != 0) drawDamage(g,    row,    col, conflict.atkDamage());
                if (conflict.defDamage() != 0) drawDamage(g, defRow, defCol, conflict.defDamage());
                if (conflict.knockback()) {
                    var arrow = "";
                    switch (direction) {
                        case NORTH -> arrow = "^";
                        case SOUTH -> arrow = "v";
                        case WEST  -> arrow = "<";
                        case EAST  -> arrow = ">";
                    }
                    var x = SIDE_PX * defCol + SIDE_PX / 2 - 3;
                    var y = SIDE_PX * defRow + 10;
                    Utils.drawOutlinedString(g, x, y, arrow);
                }
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
