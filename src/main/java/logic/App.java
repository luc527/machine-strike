package logic;

import gameconfig.GameConfigurationController;
import gameconfig.GameConfigurationView;
import utils.FileUtils;

import java.io.IOException;

// TODO use better look-and-feel

public class App
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Hello, world!");

        // TODO make (at least) 10 types of machines
        //   6 from chess, what else?

        // TODO make 5 boards
        
        var machines = FileUtils.readAll("./assets/machines.json", Machine::fromJsonElement);
        var boards = FileUtils.readAll("./assets/boards.json", Board::fromJsonElement);
        machines.forEach(System.out::println);
        boards.forEach(b -> {
            System.out.println(b);
            System.out.println();
        });
        

        var controller = new GameConfigurationController();
        var view = new GameConfigurationView(controller, boards);
        view.show();
    }
}
