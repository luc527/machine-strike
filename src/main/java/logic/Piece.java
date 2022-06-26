package logic;

// A piece is actually stored in a position in the board,
// while the Machine only serves as a type/model/prototype

import logic.turn.PieceTurn;

public class Piece implements IPiece
{
    private final Machine   machine;
    private final Player    player;
    private       int       currentHealth;
    private       Direction direction;
    private final PieceTurn turn;

    public Piece(Machine machine, Direction direction, Player player)
    {
        this.machine = machine;
        this.direction = direction;
        this.player = player;
        this.currentHealth = machine.health();
        this.turn = new PieceTurn();
    }

    public Machine machine() { return this.machine; }
    public Direction direction() { return this.direction; }
    public int health() { return this.currentHealth; }
    public Player player() { return this.player; }

    public PieceTurn getTurn() { return this.turn; }

    public IPieceTurn turn() { return this.turn; }

    public void setDirection(Direction dir) { this.direction = dir; }

    public void decreaseHealth(int amount) {
        this.currentHealth -= amount;
    }

    public boolean dead() {
        return this.currentHealth <= 0;
    }

    @Override
    public String toString()
    {
        return "Piece{" +
                "machine=" + machine +
                ", direction=" + direction +
                ", player=" + player +
                ", currentHealth=" + currentHealth +
                '}';
    }
}
