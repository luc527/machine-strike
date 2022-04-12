package assets;

import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// :PatternUsed Singleton
// But with initialization (load()) and getInstance (the()) separated,
// so we can load it in one place and then call the instance in other places
// without hvaing to handle IOExceptions

public class MachineImageMap
{
    private static final String FILENAME = "./assets/machines-images.json";
    private static MachineImageMap instance;

    private final Map<String, ImageIcon> map;

    public static void load() throws IOException
    {
        instance = new MachineImageMap();
    }

    public static ImageIcon get(String name)
    {
        if (instance == null) throw new RuntimeException("MachineImageMap not load()'ed yet!");
        return instance.map.get(name);
    }

    private MachineImageMap() throws IOException
    {
        map = new HashMap<>();
        try (var reader = new FileReader(FILENAME)) {
            var obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (var name : obj.keySet()) {
                var imageFilename = obj.get(name).getAsJsonPrimitive().getAsString();
                var icon = new ImageIcon(imageFilename);
                map.put(name, icon);
            }
        }
    }

}
