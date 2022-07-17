package logic;

// Read-only interface

public interface IGameState
{
    Board board();

    Player currentPlayer();

    IPiece pieceAt(int row, int col);

    IPiece pieceAt(Coord coord);

    boolean playerRanOutOfMoves();

    int victoryPoints(Player p);

    boolean hasWinner();

    Player getWinner();

    int currentPlayerMoves();

    int getCombatPower(Machine machine, Terrain terrain);

    int getCombatPowerDiff(Coord atkCoord, IPiece atkPiece, Direction atkDirection, Coord defCoord);

    int getAttackingPieceDamage(int combatPowerDiff);

    int getDefendingPieceDamage(int combatPowerDiff);

    Reachability actualReachability(Coord from, Coord to);
}

