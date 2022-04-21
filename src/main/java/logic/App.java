package logic;

import assets.*;
import gamebuild.placement.PiecePlacementController;
import gamebuild.selection.SelectionController;
import gamebuild.selection.SelectionView;
import gamebuild.GameBuilder;

import java.io.IOException;

// TODO use better look-and-feel

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

    public static void main(String[] args) throws IOException
    {
        loadAssets();

        // TODO use better colors (blue for marsh, gray for hill, brown for ?, blue-ish white for mountain)


        var builder = new GameBuilder();

        var placementCon = new PiecePlacementController(builder);
        var selectionCon = new SelectionController(builder, placementCon);

        var selectionView = new SelectionView(selectionCon);
        selectionView.show();

    }
}
