package logic;

// Read-only interface

public interface IGameState
{
    Board board();

    Player currentPlayer();

    IPiece pieceAt(int row, int col);

    IPiece pieceAt(Coord coord);

    // TODO refactor after implementing the AttackPerformers
    ConflictDamage getConflictDamages(
        Coord atkCoord, Coord defCoord,
        IPiece atkPiece, IPiece defPiece,
        Direction atkDirection
    );

    boolean playerRanOutOfMoves();

    int victoryPoints(Player p);

    boolean hasWinner();

    Player getWinner();

    int currentPlayerMoves();
}
