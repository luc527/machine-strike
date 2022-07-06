package logic;

import java.util.function.Function;

// TODO check if this can be thought of as a Facade or something
// (instead of passing the IGameState which has the pieceAt method, we just pass the pieceAt method directly, which is a PieceProvider)

public interface PieceProvider extends Function<Coord, IPiece>
{ }
