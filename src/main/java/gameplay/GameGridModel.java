package gameplay;

import components.boardgrid.BoardGridModel;
import logic.*;

public class GameGridModel extends BoardGridModel
{
    private final Piece[][] pieces;

    private Piece carriedPiece;
    private Direction carriedPieceDirection;
    private Coord carriedPieceOriginalPosition;

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

    public Piece pieceAt(Coord coord)
    { return pieces[coord.row()][coord.col()]; }

    public Piece pieceAt(int row, int col)
    { return pieces[row][col]; }

    public boolean carryPieceFrom(Coord coord) {
        var piece = pieceAt(coord);
        if (piece == null) return false;
        carriedPiece = piece;
        carriedPieceOriginalPosition = coord;
        carriedPieceDirection = carriedPiece.direction();
        pieces[coord.row()][coord.col()] = null;
        return true;
    }

    public void rotateCarriedPiece(boolean right) {
        if (carriedPiece == null) {
            throw new RuntimeException("rotateCarriedPiece() called but no piece is being carried");
        }
        carriedPieceDirection = carriedPieceDirection.rotated(right ? 1 : -1);
        System.out.println(carriedPieceDirection);
    }

    public Direction getCarriedPieceDirection() {
        if (carriedPiece == null) {
            throw new RuntimeException("getCarriedPieceDirection() called but no piece is being carried");
        }
        return carriedPieceDirection;
    }

    public void stopCarryingPiece() {
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

    public Piece getCarriedPiece() {
        if (carriedPiece == null) {
            throw new RuntimeException("getCarriedPiece() called but no piece is being carried");
        }
        return this.carriedPiece;
    }
}
