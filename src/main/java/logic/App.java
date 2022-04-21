package logic;

import assets.*;
import gamebuild.placement.IPiecePlacementController;
import gamebuild.placement.PiecePlacementController;
import gamebuild.selection.SelectionController;
import gamebuild.selection.SelectionView;
import gamebuild.GameBuilder;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Function;

public class App
{
    private static void loadAssets() throws IOException
    {
        Machines.load();
        Boards.load();
        MachineImageMap.load();
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

        var selectionCon = new SelectionController(builder, b -> new PiecePlacementController(b));

        var selectionView = new SelectionView(selectionCon);
        selectionView.show();

    }
}
