package logic;

// :PatternUsed Command?

// The can... methods only consider the context of the turn
// e.g maybe you canAttack() because you haven't attacked yet,
// but the piece isn't actually in front of any enemy piece to attack
// TODO should this change? maybe at least choose better names, something like hasAttacksLeft

public class PieceTurn implements IPieceTurn
{
    private int moved = 0;
    private Coord finalCoord;
    private Direction finalDirection;

    private boolean attacked = false;
    private boolean overcharged = false;
    private boolean done = false;

    // Maybe the booleans/int can be replaced by the actual thing they represent:
    // store the ConflictDamage of the attack, which is later applied to the GameState,
    // then infer 'attacked' from dmg != null

    private final Player player;
    private final Piece piece;

    public PieceTurn(Player player, Piece piece) {
        this.player = player;
        this.piece = piece;
    }

    public boolean canAttack() {
        return !overcharged && !attacked && moved < 2;
    }

    public void attack() {
        if (!canAttack()) throw new RuntimeException("Cannot attack");
        attacked = true;
    }

    public boolean canMove() {
        return !overcharged && !(attacked && moved == 1) && moved != 2;
    }

    public void move(Direction direction, int row, int col) {
        if (!canMove()) throw new RuntimeException("Cannot move");
        finalDirection = direction;

        moved++;
    }

    public void overchargedMove(Direction direction, int row, int col) {
        if (!canOvercharge()) throw new RuntimeException("Cannot overcharge (move)");
        move(direction, row, col);
        overcharged = true;
    }

    public void overchargedAttack() {
        if (!canOvercharge()) throw new RuntimeException("Cannot overcharge (attack)");
        attack();
        overcharged = true;
    }

    public boolean canOvercharge() {
        return !overcharged && (moved == 2 || (moved == 1 && attacked));
    }

    public boolean canEndTurn() {
        return moved > 1 || attacked;
    }

    public void endTurn() {
        done = true;
    }

    public boolean isDone() {
        return done;
    }
}
