package assets;

import com.google.gson.JsonParser;
import logic.Machine;
import constants.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// :PatternUsed Singleton
// But with initialization (load()) and getInstance separated,
// so we can load it in one place and then call the instance in other places
// without hvaing to handle IOExceptions

public class MachineImageMap
{
    private static final String FILENAME = "./assets/machines-images.json";
    private static MachineImageMap instance;

    private final Map<Machine, Image> map;

    public static void load() throws IOException
    {
        instance = new MachineImageMap();
    }

    public static Image get(Machine mach)
    {
        if (instance == null) throw new RuntimeException("MachineImageMap not load()'ed yet!");
        return instance.map.get(mach);
    }

    private MachineImageMap() throws IOException
    {
        map = new HashMap<>();
        try (var reader = new FileReader(FILENAME)) {
            var obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (var name : obj.keySet()) {
                var filename = obj.get(name).getAsJsonPrimitive().getAsString();
                var image = ImageIO.read(new File(filename));
                var resized = image.getScaledInstance(Constants.BOARD_SIDE_PX, Constants.BOARD_SIDE_PX, Image.SCALE_DEFAULT);
                map.put(Machines.get(name), resized);
            }
        }
    }

}
