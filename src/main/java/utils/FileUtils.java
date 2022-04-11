package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileUtils
{
    public static <K> List<K> readAll(String filename, Function<JsonElement, K> cons) throws IOException
    {
        try (var reader = new FileReader(filename)) {
            var arr = JsonParser.parseReader(reader).getAsJsonArray();
            var ks = new ArrayList<K>(arr.size());
            for (var elem : arr)
                ks.add(cons.apply(elem));
            return ks;
        }
    }
}
