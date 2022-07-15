package gameplay;

import logic.*;
import logic.MovResponse;

import java.util.ArrayList;
import java.util.List;

public class GameController implements IGameController
{
    private final List<GameObserver> observers;
    private final GameState game;

    private IPiece selectedPiece;
    private Coord selectedPieceSource;

    public GameController(GameState game)
    {
        this.game = game;
        observers = new ArrayList<>();
    }

    @Override
    public void attach(GameObserver observer)
    { observers.add(observer); }

    @Override
    public IGameState getGameState()
    { return game; }

    @Override
    public void startGame()
    { observers.forEach(o -> o.start(game.currentPlayer())); }

    @Override
    public boolean selectPiece(int row, int col)
    {
        var piece = game.pieceAt(row, col);
        if (piece == null) return false;
        if (piece.player() != game.currentPlayer()) return false;

        var pieceCoord = Coord.create(row, col);
        var movementRange = piece.machine().movementRange();

        this.selectedPiece = piece;
        this.selectedPieceSource = Coord.create(row, col);
        observers.forEach(o -> o.pieceSelected(row, col, game.pieceAt(row, col)));
        return true;
    }

    @Override
    public void unselectPiece()
    {
        if (selectedPiece == null) return;
        observers.forEach(GameObserver::pieceUnselected);

        //TODO are these =null really needed?
        selectedPiece = null;
        selectedPieceSource = null;
    }

    @Override
    public void performMovement(int row, int col, Direction dir)
    {
        var from = selectedPieceSource;
        var to = Coord.create(row, col);
        var res = game.performMovement(from, to, dir, false);

        if (res == MovResponse.OK) {
            observers.forEach(o -> o.movementPerformed(row, col, game.pieceAt(row, col)));
            selectedPiece = null;
            selectedPieceSource = null;

            if (game.hasWinner()) {
                observers.forEach(o -> o.gameWonBy(game.getWinner()));
            }
        } else {
        }
    }

    @Override
    public boolean performAttack(int row, int col, Direction dir)
    {
        var from = selectedPieceSource;
        var to   = Coord.create(row, col);
        var res  = game.performAttack(from, to, dir);

        if (res == MovResponse.OK) {
            // TODO attackPerformed? or simplify and just do gameChanged or something like that
            observers.forEach(o -> o.movementPerformed(row, col, game.pieceAt(row, col)));
            selectedPiece = null;
            selectedPieceSource = null;

            if (game.hasWinner()) {
                observers.forEach(o -> o.gameWonBy(game.getWinner()));
            }
            return true;
        } else return false;
    }

    @Override
    public void finishTurn()
    {
        if (!game.finishTurn()) return;
        observers.forEach(o -> o.turnFinished(game.currentPlayer()));
    }
}
