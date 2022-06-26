package logic.turn;

// :PatternUsed Command

import logic.Coord;
import logic.Direction;

public record Movement (
    Coord from,
    Coord to,
    Direction direction,
    boolean thenAttack
)
{

}
