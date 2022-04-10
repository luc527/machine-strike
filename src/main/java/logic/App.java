package logic;

import utils.FileUtils;

import java.io.IOException;

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
    }
}
