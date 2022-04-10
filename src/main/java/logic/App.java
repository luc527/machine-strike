package logic;

import utils.FileUtils;

import java.io.FileNotFoundException;

public class App
{
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Hello, world!");

        // TODO make (at least) 10 types of machines
        //   6 from chess, what else?
        
        var machines = FileUtils.readMachines("./assets/machines.json");
        machines.forEach(System.out::println);
    }
}
