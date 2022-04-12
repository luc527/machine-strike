package assets;

import com.google.gson.JsonParser;
import logic.Board;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Boards
{
    private static final String FILENAME = "./assets/boards.json";
    private static Boards instance;

    private List<Board> all;

    public static void load() throws IOException
    {
        instance = new Boards();
    }

    public static List<Board> all()
    {
        if (instance == null) throw new RuntimeException("Boards not load()'ed yet!");
        return Collections.unmodifiableList(instance.all);
    }

    private Boards() throws IOException
    {
        all = new ArrayList<>();
        try (var reader = new FileReader(FILENAME)) {
            var arr = JsonParser.parseReader(reader).getAsJsonArray();
            for (var elem : arr)
                all.add(Board.fromJsonElement(elem));
        }
    }
}
