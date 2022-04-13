package assets;

import com.google.gson.JsonParser;
import utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    private final Map<String, Image> map;

    public static void load() throws IOException
    {
        instance = new MachineImageMap();
    }

    public static Image get(String name)
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
                var filename = obj.get(name).getAsJsonPrimitive().getAsString();
                var image = ImageIO.read(new File(filename));
                var resized = image.getScaledInstance(Constants.TILE_WIDTH_PX, Constants.TILE_HEIGHT_PX, Image.SCALE_DEFAULT);
                map.put(name, resized);
            }
        }
    }

}
