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

        var builder = new GameBuilder();
        builder.setBoard(Boards.all().get(2));
        builder.setStartingPlayer(Player.PLAYER2);
        builder.addPiece(new Piece(Machines.get("Peon"), Direction.EAST, Player.PLAYER1), 7, 4);
        builder.addPiece(new Piece(Machines.get("Hearts"), Direction.SOUTH, Player.PLAYER2), 1, 6);
        var game = builder.build();
        var con = new GameController(game);
        new GameView(con);
        con.startGame();

    }
}
