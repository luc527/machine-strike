package logic;

import gameconfig.GameConfigurationController;
import gameconfig.GameConfigurationView;
import graphics.MachineImageMap;
import utils.FileUtils;

import java.io.IOException;

// TODO use better look-and-feel

public class App
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Hello, world!");

        // TODO make 5 boards
        
        var machines = FileUtils.readAll("./assets/machines.json", Machine::fromJsonElement);
        var boards = FileUtils.readAll("./assets/boards.json", Board::fromJsonElement);
        machines.forEach(System.out::println);
        boards.forEach(b -> {
            System.out.println(b);
            System.out.println();
        });

        MachineImageMap.load();

        // TODO make something analogous to MachineImageMap for AvailableMachines and AvailableBoards



        var controller = new GameConfigurationController();
        var view = new GameConfigurationView(controller, boards);
        view.show();
    }
}
