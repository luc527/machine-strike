package utils;

import com.google.gson.JsonParser;
import logic.Machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

public class FileUtils
{
    public static Collection<Machine> readMachines(String filename) throws FileNotFoundException
    {
        var reader = new FileReader(new File(filename));
        var arr = JsonParser.parseReader(reader).getAsJsonArray();
        var machines = new ArrayList<Machine>(arr.size());
        for (var elem : arr)
            machines.add(Machine.from(elem));
        return machines;
    }
}
