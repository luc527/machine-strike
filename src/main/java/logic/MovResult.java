package logic;

import java.util.List;

// We need to also return the pieces because we call dealDamage which removed the pieces in the coord form the game

public class MovResult
{
    private final MovResponse res;
    private final Coord atkCoord;

    // Parallel lists
    private final List<Coord> defCoords;
    private final List<IPiece> defPieces;

    public MovResult(Coord attackingPieceCoord, List<Coord> defendingPieceCoords, List<IPiece> defendingPieces)
    {
        this.res = MovResponse.OK;
        this.atkCoord = attackingPieceCoord;
        this.defCoords = defendingPieceCoords;
        this.defPieces = defendingPieces;
    }

    public MovResult(MovResponse response) {
        if (response == MovResponse.OK) {
            throw new RuntimeException("Single-argument MovResult only for non-ok MovResponse");
        }
        this.res = response;
        this.atkCoord = null;
        this.defCoords = null;
        this.defPieces = null;
    }

    // If !success(), then neither attackingPieceCoord() nor defendingPieceCoords() make sense to use

    public boolean success() { return res == MovResponse.OK; }

    public MovResponse response() { return res; }

    public Coord attackingPieceCoord() {
        if (!success()) {
            throw new RuntimeException("Unsuccessful movement has no attacking piece coord");
        }
        return atkCoord;
    }

    public List<Coord> defendingPieceCoords() {
        if (!success()) {
            throw new RuntimeException("Unsuccessful movement has no defending pieces");
        }
        return defCoords;
    }

    public List<IPiece> defendingPieces() {
        if (!success()) {
            throw new RuntimeException("Unsuccessful movement has no defending pieces");
        }
        return defPieces;
    }
}
