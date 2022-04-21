package assets;

import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultMachineInventory
{
    private static final String FILENAME = "./assets/machine-inventory.json";
    private static DefaultMachineInventory instance;

    private final Map<String, Integer> map;

    public static void load() throws IOException
    {
        instance = new DefaultMachineInventory();
    }

    public static int get(String name)
    {
        if (instance == null) throw new RuntimeException("MachineInventory not load()'ed yet!");
        return instance.map.get(name);
    }

    private DefaultMachineInventory() throws IOException
    {
        map = new HashMap<>();
        try (var reader = new FileReader(FILENAME)) {
            var obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (var name : obj.keySet()) {
                var amount = obj.get(name).getAsJsonPrimitive().getAsInt();
                map.put(name, amount);
            }
        }
    }

}
