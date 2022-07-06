package logic;

public enum MovResponse
{
    FROM_OUT_OF_BOUNDS,
    FROM_EMPTY,
    PLAYER_MISMATCH,
    TO_OUT_OF_BOUNDS,
    TO_NOT_EMPTY,
    NO_MOVES_LEFT,
    OUT_OF_REACH,
    DIDNT_MOVE,

    ATK_OUT_OF_BOUNDS,
    ATK_EMPTY,
    ATK_FRIEND,
    LANDS_ON_NOT_EMPTY,

    OK,
}