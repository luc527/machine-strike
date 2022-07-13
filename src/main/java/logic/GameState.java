package logic;

import constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class GameState implements IGameState
{
    public static final int OVERCHARGE_DAMAGE = 2;
    private static final int ROWS = Constants.BOARD_ROWS;
    private static final int COLS = Constants.BOARD_COLS;
    private static final int MAX_MOVES_PER_PLAYER = 2;

    private final Board board;
    private final Piece[][] matrix;
    private final List<Piece> pieces;

    private Player currentPlayer;
    private int currentPlayerMoves = 0;

    private int p1victoryPoints = 0;
    private int p2victoryPoints = 0;

    private Player winner = null;

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

    private static Reachability reachability(Coord center, Coord coord, int movementRange)
    {
        var dist = Math.abs(center.row() - coord.row()) + Math.abs(center.col() - coord.col());
        if (dist > movementRange + 1) return Reachability.OUT;
        if (dist == movementRange + 1) return Reachability.IN_RUNNING;
        return Reachability.IN;
    }

    public static boolean inbounds(Coord coord)
    {
        return inbounds(coord.row(), coord.col());
    }

    public static boolean inbounds(int row, int col)
    {
        return row >= 0 && row < Constants.BOARD_ROWS
            && col >= 0 && col < Constants.BOARD_COLS;
    }

    public static Direction getDefendingPoint(Direction atkDirection, Direction defDirection)
    {
        var defPoint = atkDirection.opposite();
        while (defDirection != Direction.NORTH) {
            defDirection = defDirection.cycle(false);
            defPoint     = defPoint.cycle(true);
        }
        return defPoint;
    }

    public int getCombatPower(Machine machine, Terrain terrain)
    {
        return machine.attackPower()
             + machine.type().combatPowerOffset(terrain);
    }

    public static int getAttackingCombatPower(Machine atkMachine, Terrain atkTerrain)
    {
        return atkMachine.attackPower()
             + atkMachine.type().combatPowerOffset(atkTerrain)
             + atkMachine.point(Direction.NORTH).combatPowerOffset();  // Pieces always attack from their front/north
    }

    public static int getDefendingCombatPower(Machine defMachine, Terrain defTerrain, Direction defPoint)
    {
        return defMachine.type().combatPowerOffset(defTerrain)
             + defMachine.point(defPoint).combatPowerOffset();
    }

    public Piece getPiece(int r, int c) { return matrix[r][c]; }
    public Piece getPiece(Coord c) { return getPiece(c.row(), c.col()); }

    public Board board() { return board; }
    public Player currentPlayer() { return currentPlayer; }
    public IPiece pieceAt(int r, int c) { return getPiece(r, c); }
    public IPiece pieceAt(Coord c) { return getPiece(c); }

    public int currentPlayerMoves() { return currentPlayerMoves; }
    public boolean playerRanOutOfMoves() { return currentPlayerMoves >= MAX_MOVES_PER_PLAYER; }

    public int victoryPoints(Player p) { return p == Player.PLAYER1 ? p1victoryPoints : p2victoryPoints; }

    public void setPiece(int r, int c, Piece p) { matrix[r][c] = p; }
    public void setPiece(Coord c, Piece p) { setPiece(c.row(), c.col(), p); }

    public void movePiece(Coord src, Coord dst)
    {
        var piece = getPiece(src);
        setPiece(src, null);
        setPiece(dst, piece);
    }

    public int getCombatPowerDiff(Coord atkCoord, IPiece atkPiece, Direction atkDirection, Coord defCoord)
    {
        var atkTerrain = board.get(atkCoord);
        var defTerrain = board.get(defCoord);

        var atkMachine = atkPiece.machine();
        var defPiece = pieceAt(defCoord);
        var defMachine = defPiece.machine();

        var defPoint = getDefendingPoint(atkDirection, defPiece.direction());
        var atkCombatPower = getAttackingCombatPower(atkMachine, atkTerrain);
        var defCombatPower = getDefendingCombatPower(defMachine, defTerrain, defPoint);

        return atkCombatPower - defCombatPower;
    }

    public int getAttackingPieceDamage(int diff)
    {
        return diff < 0 ? -diff : 0;
    }

    public int getDefendingPieceDamage(int diff)
    {
        return diff > 0 ? diff : 0;
    }

    public Reachability reachabilityConsideringStamina(Coord from, Coord to)
    {
        var piece = pieceAt(from);
        if (piece == null) throw new RuntimeException("Testing reachability w/ stamina from a coordinate without piece!");
        var stamina = piece.stamina();
        if (!stamina.canWalk()) return Reachability.OUT;
        var reach = reachability(from, to, piece.machine().movementRange());
        if (reach.inRunning() && !stamina.canRun()) return Reachability.OUT;
        return reach;
    }

    public boolean finishTurn()
    {
        if (winner != null) throw new RuntimeException("Game has already been won by " + winner);
        if (currentPlayerMoves == 0) return false;
        currentPlayer = currentPlayer.next();
        currentPlayerMoves = 0;
        pieces.forEach(p -> p.getStamina().reset());
        return true;
    }

    public MovResponse performMovement(Coord from, Coord to, Direction dir, boolean precedingAttack)
    {
        if (winner != null) throw new RuntimeException("Game has already been won by " + winner);
        if (playerRanOutOfMoves()) return MovResponse.NO_MOVES_LEFT;

        if (!inbounds(from)) return MovResponse.FROM_OUT_OF_BOUNDS;
        if (!inbounds(to)) return MovResponse.TO_OUT_OF_BOUNDS;

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
        var reach = reachability(from, to, range);
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
            dealDamage(to, OVERCHARGE_DAMAGE);
        }
        checkForWinner();

        return MovResponse.OK;
    }

    /**
     * "from" and "to" represent an optional movement preceding the attack,
     * and not the position of the attacking piece and the position of the attacked piece,
     * as it may look at first glance.
     */
    public MovResponse performAttack(Coord from, Coord to, Direction dir)
    {
        if (winner != null) throw new RuntimeException("Game has already been won by " + winner);
        var moveBefore = !to.equals(from);

        if (moveBefore) {
            var res = performMovement(from, to, dir, true);
            if (res != MovResponse.OK || winner != null) {
                return res;
            }
        }

        // TODO problem with this implementation: if the move can be performed fine, but the attack not,
        //  then the move will be performed but the attack wont
        //  when it should be like a transaction (atomic, either the whole thing works or nothing happens)

        var atkCoord = to;
        var atkPiece = getPiece(atkCoord);

        // The piece might've died if the movement was overcharged, so we can't attack anymore
        if (atkPiece == null) return MovResponse.OK;

        var turn = atkPiece.getStamina();
        if (!turn.canAttack()) return MovResponse.NO_MOVES_LEFT;

        var attack = atkPiece.machine().type();

        var res = attack.performAttack(this, atkCoord, dir);
        if (res != MovResponse.OK) return res;

        if (!atkPiece.dead()) {
            // If the piece moved beforehand, this is redundant,
            // but not every attack is preceded by a movement
            atkPiece.setDirection(dir);
        }

        turn.attack();
        if (turn.overcharged()) {
            var attackerCoord = attack.attackerFinalPosition();
            // Piece might've died in the attack
            if (!atkPiece.dead()) {
                dealDamage(attackerCoord, OVERCHARGE_DAMAGE);
            }
        }

        if (!moveBefore) currentPlayerMoves++;

        checkForWinner();

        return MovResponse.OK;
    }

    public void dealDamage(Coord coord, int damage)
    {
        var piece = getPiece(coord);
        piece.decreaseHealth(damage);
        if (piece.dead()) {
            setPiece(coord, null);
            var vp = piece.machine().victoryPoints();
            if (piece.player().equals(Player.PLAYER1)) {
                p2victoryPoints += vp;
            } else {
                p1victoryPoints += vp;
            }
        }
        checkForWinner();
    }

    public Player checkForWinner()
    {
        if (winner != null) return winner;

        var p1canWin = false;
        var p2canWin = false;

        if (p1victoryPoints >= 7) p1canWin = true;
        if (p2victoryPoints >= 7) p2canWin = true;

        var p1hasPieces = false;
        var p2hasPieces = false;

        for (var piece : pieces) {
            if (piece.dead()) continue;
            if (piece.player().equals(Player.PLAYER1)) p1hasPieces = true;
            if (piece.player().equals(Player.PLAYER2)) p2hasPieces = true;
        }

        if (!p1hasPieces) p2canWin = true;
        if (!p2hasPieces) p1canWin = true;

        if (p1canWin && p2canWin) {
            winner = currentPlayer;
        } else if (p1canWin) {
            winner = Player.PLAYER1;
        } else if (p2canWin) {
            winner = Player.PLAYER2;
        }
        return winner;
    }

    public boolean hasWinner() { return winner != null; }

    public Player getWinner() { return winner; }

}
