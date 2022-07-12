package gameplay;

import components.boardgrid.BoardGridModel;
import logic.*;

public class GameGridModel extends BoardGridModel
{
    private final IPiece[][] pieces;

    private final IGameState game;

    private IPiece carriedPiece;
    private Direction carriedPieceDirection;
    private Coord carriedPieceOriginalPosition;

    public GameGridModel(Board board, IGameState game)
    {
        super(board);

        this.game = game;

        pieces = new Piece[ROWS][];
        for (var row = 0; row < ROWS; row++)
            pieces[row] = new Piece[COLS];

        syncPieces();
    }

    public void syncPieces()
    {
        for (var row = 0; row < ROWS; row++) {
            for (var col = 0; col < COLS; col++) {
                var piece = game.pieceAt(row, col);
                pieces[row][col] = piece;
            }
        }
    }

    public IGameState game() { return game; }

    public IPiece pieceAt(Coord coord) { return pieces[coord.row()][coord.col()]; }
    public IPiece pieceAt(int row, int col) { return pieces[row][col]; }

    public void carryPieceFrom(Coord coord)
    {
        var piece = pieceAt(coord);
        if (piece == null) return;
        carriedPiece = piece;
        carriedPieceOriginalPosition = coord;
        carriedPieceDirection = carriedPiece.direction();
        pieces[coord.row()][coord.col()] = null;
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


    public Coord getCarriedPieceSource()
    {
        if (carriedPiece == null) {
            throw new RuntimeException("getCarriedPieceSource() called but no piece is being carried");
        }
        return carriedPieceOriginalPosition;
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

    @Override
    public boolean isAvailable(int row, int col)
    {
        var from = carriedPieceOriginalPosition;
        var to   = Coord.create(row, col);
        return !isCarryingPiece() || !game.reachabilityConsideringStamina(from, to).out();
    }

    public Reachability isReachable(int row, int col)
    {
        var from = carriedPieceOriginalPosition;
        var to   = Coord.create(row, col);
        return game.reachabilityConsideringStamina(from, to);
    }

}
