package gameplay;

import components.boardgrid.BoardGridModel;
import logic.Board;
import logic.Coord;
import logic.Piece;
import logic.PieceProvider;

public class GameGridModel extends BoardGridModel
{
    private final Piece[][] pieces;

    public GameGridModel(Board board, PieceProvider pieceProvider)
    {
        super(board);

        pieces = new Piece[ROWS][];
        for (var row = 0; row < ROWS; row++)
            pieces[row] = new Piece[COLS];

        syncPieces(pieceProvider);
    }

    private void syncPieces(PieceProvider pieceProvider)
    {
        for (var row = 0; row < ROWS; row++)
            for (var col = 0; col < COLS; col++) {
                var piece = pieceProvider.apply(Coord.create(row, col));
                pieces[row][col] = piece;
            }

    }

    public Piece pieceAt(int row, int col)
    { return pieces[row][col]; }

    public Piece pieceUnderCursor()
    { return pieceAt(cursor.row(), cursor.col()); }
}
