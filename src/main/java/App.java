import assets.*;
import gamebuild.playerAndBoardSelection.PlayerAndBoardSelectionController;
import gamebuild.playerAndBoardSelection.PlayerAndBoardSelectionView;
import gamebuild.GameBuilder;
import gameplay.GameController;
import gameplay.GameView;
import logic.Direction;
import logic.Piece;
import logic.Player;

import javax.swing.*;
import java.io.IOException;

public class App
{
    private static void loadAssets() throws IOException
    {
        Machines.load();  // must come before MachineImageMap
        MachineImageMap.load();
        Boards.load();
        TerrainColorMap.load();
        DefaultMachineInventory.load();
    }

    public static void testGame()
    {
        var builder = new GameBuilder();
        builder.setBoard(Boards.all().get(2));

        builder.setStartingPlayer(Player.PLAYER2);

        builder.addPiece(new Piece(Machines.get("Peon"), Direction.EAST, Player.PLAYER1), 7, 4);
        builder.addPiece(new Piece(Machines.get("Spades"), Direction.NORTH, Player.PLAYER1), 7, 7);
        builder.addPiece(new Piece(Machines.get("Bishop"), Direction.SOUTH, Player.PLAYER1), 6, 0);
        builder.addPiece(new Piece(Machines.get("King"), Direction.SOUTH, Player.PLAYER1), 7, 0);
        builder.addPiece(new Piece(Machines.get("Diamonds"), Direction.SOUTH, Player.PLAYER1), 7, 6);

        builder.addPiece(new Piece(Machines.get("Hearts"), Direction.SOUTH, Player.PLAYER2), 1, 6);
        builder.addPiece(new Piece(Machines.get("Knight"), Direction.WEST, Player.PLAYER2), 0, 3);
        builder.addPiece(new Piece(Machines.get("Tower"), Direction.EAST, Player.PLAYER2), 0, 6);
        builder.addPiece(new Piece(Machines.get("Queen"), Direction.EAST, Player.PLAYER2), 0, 5);

        var game = builder.build();
        var con = new GameController(game);
        new GameView(con);
        con.startGame();
    }

    public static void main(String[] args)
    throws Exception
    {
        loadAssets();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // TODO use better colors (blue for marsh, gray for hill, brown for ?, blue-ish white for mountain)

        //var builder = new GameBuilder();
        //var con = new PlayerAndBoardSelectionController(builder);
        //var view = new PlayerAndBoardSelectionView(con);
        //view.show();

        testGame();
    }
}
