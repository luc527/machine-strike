package gameplay;

import components.boardgrid.BoardGridModel;
import logic.*;
import logic.turn.ConflictResult;
import logic.turn.ITurn;

import java.util.function.Function;

public class GameGridModel extends BoardGridModel
{
    private final IPiece[][] pieces;

    private IPiece carriedPiece;
    private Direction carriedPieceDirection;
    private Coord carriedPieceOriginalPosition;
    private Function<Coord, Reachability> reachabilityFn = x -> Reachability.IN;
    private ITurn currentTurn;

    public interface ConflictResultFunction {
        ConflictResult apply(Coord atkCoord, Coord defCoord, IPiece atkPiece, IPiece defPiece, Direction atkDirection);
    }

    private ConflictResultFunction conflictResultFn;

    public GameGridModel(Board board, PieceProvider pieceProvider)
    {
        super(board);

        pieces = new Piece[ROWS][];
        for (var row = 0; row < ROWS; row++)
            pieces[row] = new Piece[COLS];

        syncPieces(pieceProvider);
    }

    public void syncPieces(PieceProvider pieceProvider)
    {
        for (var row = 0; row < ROWS; row++)
            for (var col = 0; col < COLS; col++) {
                var piece = pieceProvider.apply(Coord.create(row, col));
                pieces[row][col] = piece;
            }

    }

    public IPiece pieceAt(Coord coord)
    { return pieces[coord.row()][coord.col()]; }

    public IPiece pieceAt(int row, int col)
    { return pieces[row][col]; }

    public boolean carryPieceFrom(Coord coord)
    {
        var piece = pieceAt(coord);
        if (piece == null) return false;
        carriedPiece = piece;
        carriedPieceOriginalPosition = coord;
        carriedPieceDirection = carriedPiece.direction();
        pieces[coord.row()][coord.col()] = null;
        return true;
    }

    public void rotateCarriedPiece(boolean right)
    {
        if (carriedPiece == null) {
            throw new RuntimeException("rotateCarriedPiece() called but no piece is being carried");
        }
        carriedPieceDirection = carriedPieceDirection.cycle(right);
        onMove.run();
    }

    public Direction getCarriedPieceDirection()
    {
        if (carriedPiece == null) {
            throw new RuntimeException("getCarriedPieceDirection() called but no piece is being carried");
        }
        return carriedPieceDirection;
    }

    public void stopCarryingPiece()
    {
        if (carriedPiece == null) {
            throw new RuntimeException("stopCarryingPiece() called but no piece is being carried");
        }
        var orig = carriedPieceOriginalPosition;
        pieces[orig.row()][orig.col()] = carriedPiece;
        carriedPiece = null;
        carriedPieceDirection = null;
        carriedPieceOriginalPosition = null;
    }

    public boolean isCarryingPiece()
    { return carriedPiece != null; }

    public IPiece getCarriedPiece()
    {
        if (carriedPiece == null) {
            throw new RuntimeException("getCarriedPiece() called but no piece is being carried");
        }
        return this.carriedPiece;
    }

    public Coord getAttackedCoord()
    {
        return cursor.moved(carriedPieceDirection);
    }

    public IPiece getAttackedPiece()
    {
        var attackedCoord = getAttackedCoord();
        if (!GameLogic.inbounds(attackedCoord)) return null;
        var piece = pieceAt(attackedCoord);
        if (piece == null) return null;
        if (piece.player().equals(carriedPiece.player())) return null;
        return piece;
    }

    public boolean isCarriedPieceAttacking()
    {
        return getAttackedPiece() != null;
    }

    public void setReachabilityFunction(Function<Coord, Reachability> isReachable)
    {
        this.reachabilityFn = isReachable;
    }

    public void setConflictResultFunction(ConflictResultFunction fn)
    {
        this.conflictResultFn = fn;
    }

    @Override
    public boolean isAvailable(int row, int col)
    {
        return !isCarryingPiece() || !reachabilityFn.apply(Coord.create(row, col)).out();
    }

    public Reachability isReachable(int row, int col)
    {
        return reachabilityFn.apply(Coord.create(row, col));
    }

    public ConflictResult getConflictResult(Coord atkCoord, Coord defCoord, IPiece atkPiece, IPiece defPiece, Direction atkDirection)
    {
        return this.conflictResultFn.apply(atkCoord, defCoord, atkPiece, defPiece, atkDirection);
    }

    public void setCurrentTurn(ITurn turn)
    { this.currentTurn = turn; }

    public ITurn getCurrentTurn()
    { return this.currentTurn; }
}
