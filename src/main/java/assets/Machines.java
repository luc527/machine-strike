package assets;

import com.google.gson.JsonParser;
import logic.Machine;

import java.util.*;

import java.io.FileReader;
import java.io.IOException;

// :PatternUsed Singleton

public class Machines
{
    private static final String FILENAME = "./assets/machines.json";
    private static Machines instance;

    private List<Machine> all;
    private Map<String, Machine> byName;

    public static void load() throws IOException
    {
        instance = new Machines();
    }

    public static List<Machine> all()
    {
        if (instance == null) throw new RuntimeException("Machines not load()'ed yet!");
        return instance.all;
    }

    public static Machine get(String name)
    {
        if (instance == null) throw new RuntimeException("Machines not load()'ed yet!");
        return instance.byName.get(name);
    }

    public static Set<String> allNames()
    {
        if (instance == null) throw new RuntimeException("Machines not load()'ed yet!");
        return instance.byName.keySet();
    }

    private Machines() throws IOException
    {
        all = new ArrayList<>();
        byName = new HashMap<>();

        try (var reader = new FileReader(FILENAME)) {
            var arr = JsonParser.parseReader(reader).getAsJsonArray();
            for (var elem : arr) {
                var machine = Machine.fromJsonElement(elem);
                all.add(machine);
                byName.put(machine.name(), machine);
            }
        }

        all = Collections.unmodifiableList(all);
    }
}
