package logic;

import constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class GameState implements IGameState
{
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int MAX_MOVES_PER_PLAYER = 2;

    private final Board board;
    private final Piece[][] matrix;
    private final List<Piece> pieces;

    private Player currentPlayer;
    private int currentPlayerMoves;

    public GameState(Player startingPlayer, Board board, Piece[][] startingPieces)
    {
        this.currentPlayer = startingPlayer;
        this.currentPlayerMoves = 0;
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

    public Piece getPiece(int r, int c) { return matrix[r][c]; }
    public Piece getPiece(Coord c) { return getPiece(c.row(), c.col()); }
    public void setPiece(int r, int c, Piece p) { matrix[r][c] = p; }
    public void setPiece(Coord c, Piece p) { setPiece(c.row(), c.col(), p); }

    public void movePiece(Coord src, Coord dst)
    {
        var piece = getPiece(src);
        setPiece(src, null);
        setPiece(dst, piece);
    }

    public Board board() { return board; }
    public Player currentPlayer() { return currentPlayer; }
    public IPiece pieceAt(int r, int c) { return getPiece(r, c); }
    public IPiece pieceAt(Coord c) { return getPiece(c); }

    public boolean playerRanOutOfMoves() { return currentPlayerMoves >= MAX_MOVES_PER_PLAYER; }

    public ConflictDamage getConflictDamages(
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

        return new ConflictDamage(atkDamage, defDamage, knockback);
    }

    public boolean finishTurn()
    {
        if (currentPlayerMoves == 0) return false;
        currentPlayer = currentPlayer.next();
        currentPlayerMoves = 0;
        pieces.forEach(p -> p.getStamina().reset());
        return true;
    }

    public MovResponse performMovement(Coord from, Coord to, Direction dir, boolean precedingAttack)
    {
        if (playerRanOutOfMoves()) return MovResponse.NO_MOVES_LEFT;

        if (!GameLogic.inbounds(from)) return MovResponse.FROM_OUT_OF_BOUNDS;
        if (!GameLogic.inbounds(to)) return MovResponse.TO_OUT_OF_BOUNDS;

        var piece = getPiece(from);
        if (piece == null) return MovResponse.FROM_EMPTY;
        if (!piece.player().equals(currentPlayer)) return MovResponse.PLAYER_MISMATCH;

        var dirChanged = dir != piece.direction();
        if (to.equals(from) && !dirChanged) return MovResponse.DIDNT_MOVE;

        if (!to.equals(from) && getPiece(to) != null) return MovResponse.TO_NOT_EMPTY;

        var turn = piece.getStamina();
        var canWalk = turn.canWalk();
        var canRun = turn.canRun() && !precedingAttack;

        if (!canWalk) return MovResponse.NO_MOVES_LEFT;

        var range = piece.machine().movementRange();
        var reach = GameLogic.reachability(from, to, range);
        if (reach.out()) return MovResponse.OUT_OF_REACH;

        var walk = reach.in();
        var run = reach.inRunning();

        if (run && !canRun) return MovResponse.OUT_OF_REACH;

        currentPlayerMoves++;

        setPiece(from, null);
        setPiece(to, piece);
        piece.setDirection(dir);

        if (walk) turn.walk();
        if (run)  turn.run();

        if (turn.overcharged()) {
            dealDamage(to, GameLogic.OVERCHARGE_DAMAGE);
        }

        return MovResponse.OK;
    }

    /**
     * "from" and "to" represent an optional movement preceding the attack,
     * and not the position of the attacking piece and the position of the attacked piece,
     * as it may look at first glance.
     */
    public MovResponse performAttack(Coord from, Coord to, Direction dir)
    {
        var moveBefore = !to.equals(from);

        if (moveBefore) {
            var res = performMovement(from, to, dir, true);
            if (res != MovResponse.OK) return res;
        }

        var atkCoord = to;
        var atkPiece = getPiece(atkCoord);

        // The piece might've died if the movement was overcharged
        if (atkPiece == null) return MovResponse.OK;

        atkPiece.setDirection(dir);

        var turn = atkPiece.getStamina();
        if (!turn.canAttack()) return MovResponse.NO_MOVES_LEFT;

        var attack = atkPiece.machine().attackType();

        var res = attack.performAttack(this, atkCoord, dir);
        if (res != MovResponse.OK) return res;

        turn.attack();
        if (turn.overcharged()) {
            var attackerCoord = attack.attackerFinalPosition();
            dealDamage(attackerCoord, GameLogic.OVERCHARGE_DAMAGE);
        }


        if (!moveBefore) currentPlayerMoves++;

        return MovResponse.OK;
    }

    public void dealDamage(Coord coord, int damage)
    {
        var piece = getPiece(coord);
        piece.decreaseHealth(damage);
        if (piece.dead()) {
            setPiece(coord, null);
        }
    }
}
