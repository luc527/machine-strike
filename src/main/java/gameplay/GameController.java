package gameplay;

import components.boardgrid.BoardGridModel;
import logic.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController implements IGameController
{
    private final List<GameObserver> observers;
    private final GameState game;
    private final BoardGridModel grid;

    public GameController(GameState game)
    {
        this.game = game;
        this.observers =  new ArrayList<>();
        this.grid = new BoardGridModel(
                game.board(),
                (coord) -> Optional.ofNullable(game.pieceAt(coord.row(), coord.col()))
        );
    }

    public void attach(GameObserver observer)
    {
        observers.add(observer);
    }

    public void startGame()
    {
        observers.forEach(o -> o.start(game.currentPlayer(), grid));
    }

    @Override
    public PieceSelectionResponse selectPiece()
    {
        var cursor = grid.getCursor();
        var piece = game.pieceAt(cursor.row(), cursor.col());
        if (piece == null) return PieceSelectionResponse.EMPTY;
        if (piece.player() != game.currentPlayer()) return PieceSelectionResponse.ENEMY;
        grid.carryPiece(piece);
        return PieceSelectionResponse.OK;
    }

}
