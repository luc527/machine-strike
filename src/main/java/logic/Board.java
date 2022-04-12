package logic;

import com.google.gson.JsonElement;
import utils.Constants;

public class Board
{
    private static final int HEIGHT = Constants.BOARD_ROWS;
    private static final int WIDTH = Constants.BOARD_COLS;

    private final Terrain[][] board = new Terrain[HEIGHT][WIDTH];

    public Board(Terrain[][] board)
    {
        for (var i = 0; i < HEIGHT; i++)
            for (var j = 0; j < WIDTH; j++)
                this.board[i][j] = board[i][j];
    }
    public Terrain get(int row, int col) { return board[row][col]; }

    public static Board fromJsonElement(JsonElement json)
    {
        var rows = json.getAsJsonArray();
        var board = new Terrain[HEIGHT][WIDTH];
        for (var i = 0; i < HEIGHT; i++) {
            var cells = rows.get(i).getAsJsonPrimitive().getAsString();
            for (var j = 0; j < WIDTH; j++) {
                board[i][j] = Terrain.from(cells.charAt(j));
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
                sb.append(board[i][j].toChar());
            }
        }
        return sb.toString();
    }
}
