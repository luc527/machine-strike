package logic;

import com.google.gson.JsonElement;

public class Board
{
    private static final int HEIGHT = 8;
    private static final int WIDTH = 8;

    private final Terrain[][] board = new Terrain[HEIGHT][WIDTH];

    public Board(Terrain[][] board)
    {
        for (var i = 0; i < HEIGHT; i++)
            for (var j = 0; j < WIDTH; j++)
                this.board[i][j] = board[i][j];
    }

    public int height() { return HEIGHT; }
    public int width() { return WIDTH; }
    public Terrain get(int row, int col) { return board[row][col]; }

    public static Board fromJsonElement(JsonElement json)
    {
        var rows = json.getAsJsonArray();
        var board = new Terrain[HEIGHT][WIDTH];
        for (var i = 0; i < HEIGHT; i++) {
            var cells = rows.get(i).getAsJsonPrimitive().getAsString();
            for (var j = 0; j < WIDTH; j++) {
                var cell = cells.charAt(j);
                switch (cell) {
                    case 'c': board[i][j] = Terrain.CHASM; break;
                    case 'm': board[i][j] = Terrain.MARSH; break;
                    case 'g': board[i][j] = Terrain.GRASSLAND; break;
                    case 'f': board[i][j] = Terrain.FOREST; break;
                    case 'h': board[i][j] = Terrain.HILL; break;
                    case 'M': board[i][j] = Terrain.MOUNTAIN; break;
                    default:  throw new RuntimeException("Unrecognized character while parsing board: " + cell);
                }
            }
        }
        return new Board(board);
    }

    public String toString()
    {
        var sb = new StringBuilder();
        var linesep = "";
        for (var i = 0; i < HEIGHT; i++) {
            sb.append(linesep);
            linesep = "\n";
            for (var j = 0; j < WIDTH; j++) {
                var ch = '-';
                switch (board[i][j]) {
                    case CHASM:     ch = 'c'; break;
                    case MARSH:     ch = 'm'; break;
                    case GRASSLAND: ch = 'g'; break;
                    case FOREST:    ch = 'f'; break;
                    case HILL:      ch = 'h'; break;
                    case MOUNTAIN:  ch = 'M'; break;
                }
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
