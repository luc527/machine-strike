package logic;

import constants.Constants;
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
                var dmg = GameLogic.conflictDamage(
                    piece.machine(), dir, board.get(to),
                    defPiece.machine(), board.get(defCoord)
                );
                defPiece.decreaseHealth(dmg.defDamage);
                if (defPiece.dead()) setPiece(defCoord, null);
                // TODO check for win
                piece.decreaseHealth(dmg.atkDamage);
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

        if (reach == Reachability.OUT) return MovResponse.OUT_OF_REACH;

        var walk = reach == Reachability.IN;
        var run = reach == Reachability.IN_RUNNING;

        if (run && !turn.canRun()) return MovResponse.OUT_OF_REACH;
        if (walk && !turn.canWalk()) return MovResponse.OUT_OF_REACH;

        if (!speculative) {
            setPiece(from, null);
            setPiece(to, piece);
            piece.setDirection(dir);

            if (run) turn.run();
            if (walk) turn.walk();

            if (turn.overcharged()) {
                piece.decreaseHealth(2);
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
