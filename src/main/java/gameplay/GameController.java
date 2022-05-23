package gameplay;

import logic.Board;
import logic.Coord;
import logic.GameState;
import logic.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameController implements IGameController
{
    private final List<GameObserver> observers;
    private final GameState game;

    private Piece selectedPiece;
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
    public void selectPiece(int row, int col)
    {
        var piece = game.pieceAt(row, col);
        if (piece == null) return;
        if (piece.player() != game.currentPlayer()) return;
        selectedPiece = piece;
        availablePositions = GameState.generateAvailablePositions(row, col, piece.machine().movementRange());
        observers.forEach(o -> o.pieceSelected(row, col, game.pieceAt(row, col), availablePositions));
    }

    @Override
    public void unselectPiece()
    {
        if (selectedPiece == null) return;
        selectedPiece = null;
        availablePositions = null;
        observers.forEach(o -> o.pieceUnselected());
    }
}
