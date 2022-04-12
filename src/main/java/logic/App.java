package logic;

import assets.Boards;
import assets.Machines;
import assets.MachineImageMap;
import assets.TerrainColorMap;
import gameconfig.GameConfigurationController;
import gameconfig.GameConfigurationView;

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
    }

    public static void main(String[] args) throws IOException
    {
        loadAssets();

        // TODO make 5 boards
        
        for (var m : Machines.all())
            System.out.println(m);

        for (var b : Boards.all())
            System.out.println(b);

        for (var t : Terrain.values())
            System.out.println(TerrainColorMap.get(t));

        // TODO to display the boards in the game config screen,
        //  make a SmallBoardIcon implements Icon

        var controller = new GameConfigurationController();
        var view = new GameConfigurationView(controller);
        view.show();
    }
}
