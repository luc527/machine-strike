package logic;

// A piece is actually stored in a position in the board,
// while the Machine only serves as a type/model/prototype

public class Piece
{
    private final Machine machine;
    private Direction direction;
    private Player player;
    private int currentHealth;

    public Piece(Machine machine, Direction direction, Player player)
    {
        this.machine = machine;
        this.direction = direction;
        this.player = player;
        this.currentHealth = machine.health();
    }

    public Machine machine() { return this.machine; }
    public Direction direction() { return this.direction; }
    public int health() { return this.currentHealth; }
}
