package logic;

import assets.Boards;
import assets.Machines;
import assets.MachineImageMap;
import assets.TerrainColorMap;
import gameconfig.GameConfigurationController;
import gameconfig.GameConfigurationView;
import graphics.BoardIcon;

import javax.swing.*;
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

        // TODO use better colors (blue for marsh, gray for hill, brown for ?, blue-ish white for mountain)

        var controller = new GameConfigurationController();
        var view = new GameConfigurationView(controller);
        view.show();
    }
}
