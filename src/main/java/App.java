import assets.*;
import gamebuild.playerAndBoardSelection.PlayerAndBoardSelectionController;
import gamebuild.playerAndBoardSelection.PlayerAndBoardSelectionView;
import gamebuild.GameBuilder;

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
    throws IOException,
           UnsupportedLookAndFeelException,
           ClassNotFoundException,
           InstantiationException,
           IllegalAccessException
    {
        loadAssets();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // TODO use better colors (blue for marsh, gray for hill, brown for ?, blue-ish white for mountain)

        var builder = new GameBuilder();
        var con = new PlayerAndBoardSelectionController(builder);
        var view = new PlayerAndBoardSelectionView(con);
        view.show();

    }
}
