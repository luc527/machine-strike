package logic;

import constants.Constants;
import logic.turn.ConflictResult;
import logic.turn.MovResponse;

import java.util.ArrayList;
import java.util.List;

public class GameState
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;

    private final Board board;
    private final Piece[][] matrix;
    private final List<Piece> pieces;

    private Player currentPlayer;
    private boolean currentPlayerMoved;

    public GameState(Player startingPlayer, Board board, Piece[][] startingPieces)
    {
        this.currentPlayer = startingPlayer;
        this.currentPlayerMoved = false;
        this.board = board;
        this.pieces = new ArrayList<>();

        this.matrix = new Piece[ROWS][COLS];
        for (var row = 0; row < ROWS; row++)
            for (var col = 0; col < COLS; col++) {
                var piece = startingPieces[row][col];
                matrix[row][col] = piece;
                if (piece != null) pieces.add(piece);
            }

    }

    public Player currentPlayer() { return currentPlayer; }
    public Board board() { return board; }

    private Piece getPiece(int r, int c) { return matrix[r][c]; }
    private Piece getPiece(Coord c) { return getPiece(c.row(), c.col()); }

    public IPiece pieceAt(int r, int c) { return getPiece(r, c); }
    public IPiece pieceAt(Coord c) { return getPiece(c); }

    private void setPiece(int r, int c, Piece p) { matrix[r][c] = p; }
    private void setPiece(Coord c, Piece p) { setPiece(c.row(), c.col(), p); }

    public ConflictResult conflict(
        Coord atkCoord, Coord defCoord,
        IPiece atkPiece, IPiece defPiece,
        Direction atkDirection
    ) {
        var atkTerrain = board.get(atkCoord);
        var defTerrain = board.get(defCoord);
        var atkMachine = atkPiece.machine();
        var defMachine = defPiece.machine();

        // To find out which point of the defending piece is being attacked (defPoint),
        // we start with it set as if the defending piece was laying north,
        // then correct it considering the actual rotation of the defending piece
        var defPieceDirection = defPiece.direction();
        var defPoint = atkDirection.opposite();
        while (defPieceDirection != Direction.NORTH) {
            defPieceDirection = defPieceDirection.cycle(false);
            defPoint = defPoint.cycle(true);
        }

        var atkCombatPower = atkMachine.attackPower() + atkTerrain.combatPowerOffset() + atkMachine.point(Direction.NORTH).combatPowerOffset();
        var defCombatPower = defTerrain.combatPowerOffset() + defMachine.point(defPoint).combatPowerOffset();

        var diff = atkCombatPower - defCombatPower;

        var atkDamage = 0;  // Damage to the attacking piece
        var defDamage = 0;  // Damage to the defending piece
        var knockback = false;

        if (diff < 0) {
            atkDamage = -diff;
        } else if (diff > 0) {
            defDamage = diff;
        } else {
            atkDamage = 1;
            defDamage = 1;

            var coordBehind = defCoord.moved(atkDirection);
            var canMoveBack = GameLogic.inbounds(coordBehind) && getPiece(coordBehind) == null;
            if (canMoveBack) {
                knockback = true;
            } else {
                defDamage++;
            }
        }

        return new ConflictResult(atkDamage, defDamage, knockback);
    }

    public boolean finishTurn()
    {
        if (!currentPlayerMoved) return false;
        currentPlayer = currentPlayer.next();
        currentPlayerMoved = false;
        for (var piece : pieces) piece.getTurn().reset();
        return true;
    }

    public MovResponse checkMovement(Coord from, Coord to, Direction dir, boolean thenAttack)
    { return movementImpl(from, to, dir, thenAttack, true); }

    public MovResponse performMovement(Coord from, Coord to, Direction dir, boolean thenAttack)
    { return movementImpl(from, to, dir, thenAttack, false); }

    private MovResponse movementImpl(
        Coord from,
        Coord to,
        Direction dir,
        boolean thenAttack,
        boolean speculative
    ) {
        if (!GameLogic.inbounds(from)) return MovResponse.FROM_OUT_OF_BOUNDS;
        if (!GameLogic.inbounds(to)) return MovResponse.TO_OUT_OF_BOUNDS;

        var piece = getPiece(from);
        if (piece == null) return MovResponse.FROM_EMPTY;
        if (!piece.player().equals(currentPlayer)) return MovResponse.PLAYER_MISMATCH;

        var dirChanged = dir != piece.direction();
        if (to.equals(from) && !thenAttack && !dirChanged) return MovResponse.DIDNT_MOVE;

        if (!to.equals(from) && getPiece(to) != null) return MovResponse.TO_NOT_EMPTY;

        if (!thenAttack) {
            return moveImpl(from, to, dir, speculative);
        } else {
            // 'def' as in 'defense' or 'defending'
            var defCoord = to.moved(dir);
            if (!GameLogic.inbounds(defCoord)) return MovResponse.ATK_OUT_OF_BOUNDS;

            var defPiece = getPiece(defCoord);
            if (defPiece == null) return MovResponse.ATK_EMPTY;

            if (piece.player().equals(defPiece.player())) return MovResponse.ATK_SAME_PLAYER;

            var moveRes = moveImpl(from, to, dir, speculative);
            if (moveRes != MovResponse.OK) return moveRes;

            if (!speculative) {
                var conflict = conflict(from, to, piece, defPiece, dir);
                System.out.println("Actual conflict " + conflict);
                defPiece.decreaseHealth(conflict.defDamage());
                if (defPiece.dead()) {
                    setPiece(defCoord, null);
                } else if (conflict.knockback()) {
                    setPiece(defCoord, null);
                    setPiece(defCoord.moved(dir), defPiece);
                }
                // TODO check for win
                piece.decreaseHealth(conflict.atkDamage());
                if (piece.dead()) setPiece(to, null);
                // TODO check for win

                currentPlayerMoved = true;
            }
            return MovResponse.OK;
        }
    }

    private MovResponse moveImpl(
        Coord from,
        Coord to,
        Direction dir,
        boolean speculative
    ) {
        var piece = getPiece(from);
        var turn = piece.getTurn();

        if (!turn.canWalk()) return MovResponse.NO_MOVES_LEFT;

        var range = piece.machine().movementRange();
        var reach = GameLogic.reachability(from, to, range);

        if (reach.out()) return MovResponse.OUT_OF_REACH;

        var walk = reach.in();
        var run = reach.inRunning();

        if (run && !turn.canRun()) return MovResponse.OUT_OF_REACH;
        if (walk && !turn.canWalk()) return MovResponse.OUT_OF_REACH;

        if (!speculative) {
            setPiece(from, null);
            setPiece(to, piece);
            piece.setDirection(dir);

            if (run) turn.run();
            if (walk) turn.walk();

            if (turn.overcharged()) {
                piece.decreaseHealth(GameLogic.OVERCHARGE_DAMAGE);
                if (piece.dead()) {
                    setPiece(to, null);
                    // TODO check for win
                }
            }

            currentPlayerMoved = true;
        }

        return MovResponse.OK;
    }

}
