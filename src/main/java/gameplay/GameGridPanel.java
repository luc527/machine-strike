package gameplay;

import components.Palette;
import logic.*;
import components.boardgrid.BoardGridPanel;
import logic.attackType.AttackVisitor;

import java.awt.*;
import java.util.Optional;

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
        Utils.drawOutlinedString(g, x, y, "!!!", Palette.darkYellow, Palette.yellow);
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

    protected void paintMachines(Graphics2D g)
    {
        var grid = (GameGridModel) this.grid;

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
    }

    private void paintMachinesCombatPower(Graphics2D g)
    {
        var grid = (GameGridModel) this.grid;

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

    @Override
    protected void paintComponent(Graphics G)
    {
        panelPaintComponent(G);

        var g = (Graphics2D) G;
        paintTerrain(g);
        paintAvailablePositions(g);
        paintCursor(g);
        paintMachines(g);

        var grid = (GameGridModel) this.grid;

        if (grid.isCarryingPiece()) {
            var piece = grid.getCarriedPiece();
            var direction = grid.getCarriedPieceDirection();
            var cursor = grid.getCursor();
            var row = cursor.row();
            var col = cursor.col();
            var cy = SIDE_PX * row;
            var cx = SIDE_PX * col;
            super.drawMachine(piece.machine(), direction, g, cx, cy);

            var combatPower = GameLogic.combatPower(piece.machine(), grid.getBoard().get(row, col));
            drawCombatPower(g, row, col, combatPower);

            var attack = piece.machine().attackType();
            var visitor = new AttackVisitor() {
                public int attackingPieceDamage = 0;
                private int numAttacked = 0;
                public void visitAttack(Coord coord, Optional<IPiece> optPiece) {
                    var y = coord.row() * SIDE_PX;
                    var x = coord.col() * SIDE_PX;
                    g.setColor(Palette.transparentRed);
                    g.fillRect(x, y, SIDE_PX, SIDE_PX);

                    var run = grid.isReachable(row, col).inRunning();

                    if (optPiece.isPresent() && !run) {
                        numAttacked++;
                        var defPiece = optPiece.get();
                        var conflict = grid.getConflictResult(cursor, coord, piece, defPiece, direction);
                        drawDamage(g, coord.row(), coord.col(), conflict.defDamage());

                        if (numAttacked > 1) attackingPieceDamage = 0;
                        else attackingPieceDamage = conflict.atkDamage();
                        // Otherwise there's no *one* correct conflict damage, but a different one for every pair
                    }
                }
            };
            attack.accept(visitor, grid::pieceAt, cursor, piece, direction);

            var carriedPieceDamage = visitor.attackingPieceDamage;

            var overcharge = false;
            var turn = piece.stamina();
            var reach = grid.isReachable(row, col);
            overcharge = (reach.in() && turn.walkWouldOvercharge())
                      || (reach.inRunning() && turn.runWouldOvercharge());

            var moved = !cursor.equals(grid.getCarriedPieceSource());

            var isAttacking = attack.isAttacking(grid::pieceAt, cursor, piece, direction);
            if (isAttacking) {
                overcharge = overcharge
                          || (!moved && turn.attackWouldOvercharge())
                          || ( moved && turn.walkAndAttackWouldOvercharge());
            }

            if (overcharge) {
                carriedPieceDamage += GameLogic.OVERCHARGE_DAMAGE;
                drawOverchargeWarning(g, row, col);
            }
            if (carriedPieceDamage != 0) drawDamage(g, row, col, carriedPieceDamage);

            drawHealth(g, row, col, piece.health());
        }

        paintMachinesCombatPower(g);

    }

}
