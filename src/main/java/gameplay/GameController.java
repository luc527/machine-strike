package gameplay;

import logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameController implements IGameController
{
    private final List<GameObserver> observers;
    private final GameState game;

    private Piece selectedPiece;
    private Coord selectedPieceSource;
    private Set<Coord> availablePositions; // TODO +1 range for running option

    public GameController(GameState game)
    {
        this.game = game;
        observers = new ArrayList<>();
    }

    @Override
    public Board getBoard()
    { return game.board(); }

    @Override
    public Piece pieceAt(Coord coord)
    { return game.pieceAt(coord.row(), coord.col()); }

    @Override
    public void startGame()
    { observers.forEach(o -> o.start(game.currentPlayer())); }

    @Override
    public void attach(GameObserver observer)
    { observers.add(observer); }

    @Override
    public boolean selectPiece(int row, int col)
    {
        var piece = game.pieceAt(row, col);
        if (piece == null) return false;
        if (piece.player() != game.currentPlayer()) return false;
        selectedPiece = piece;
        selectedPieceSource = Coord.create(row, col);
        availablePositions = GameLogic.generateAvailablePositions(row, col, piece.machine().movementRange());
        observers.forEach(o -> o.pieceSelected(row, col, game.pieceAt(row, col), availablePositions));
        return true;
    }

    @Override
    public boolean unselectPiece()
    {
        if (selectedPiece == null) return false;
        observers.forEach(o -> o.pieceUnselected());

        //TODO are these =null really needed? also check for placePiece
        selectedPiece = null;
        selectedPieceSource = null;
        availablePositions = null;
        return true;
    }

    @Override
    public boolean placePiece(int row, int col, Direction dir)
    {
        if (selectedPiece == null) return false;

        var destPiece = game.pieceAt(row, col);
        if (destPiece != null && destPiece != selectedPiece) return false;
        var src = selectedPieceSource;
        game.pieceAt(src.row(), src.col()).setDirection(dir);
        game.movePiece(src.row(), src.col(), row, col);
        observers.forEach(o -> o.piecePlaced(row, col, game.pieceAt(row, col)));

        selectedPiece = null;
        selectedPieceSource = null;
        availablePositions = null;
        return true;
    }
}
