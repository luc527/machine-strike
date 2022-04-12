package assets;

import com.google.gson.JsonParser;
import logic.Terrain;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;

public class TerrainColorMap
{
    private static final String FILENAME = "./assets/terrain-colors.json";

    private EnumMap<Terrain, Color> toColor;

    private static TerrainColorMap instance;

    public static void load() throws IOException
    {
        instance = new TerrainColorMap();
    }

    public static Color get(Terrain t)
    {
        if (instance == null) throw new RuntimeException("TerrainColorMap not load()'ed yet");
        return instance.toColor.get(t);
    }

    public TerrainColorMap() throws IOException
    {
        toColor = new EnumMap<>(Terrain.class);
        try (var reader = new FileReader(FILENAME)) {
            var obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (var key : obj.keySet()) {
                var terr = Terrain.from(key.charAt(0));
                var val = obj.get(key).getAsJsonPrimitive().getAsString();
                var color = new Color(Integer.parseInt(val, 16));
                toColor.put(terr, color);
            }
        }
    }
}
