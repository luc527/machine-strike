package logic.attackType;

import logic.*;

import java.util.ArrayList;
import java.util.List;

public class DashMachineType extends MachineType
{
    public DashMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(PieceProvider pieceAt, Coord from, IPiece piece, Direction dir)
    {
        // All positions in attack range (having a piece)
        // attackRange - 1 because that's the position where the piece lands

        var landingCoord = from.moved(dir, attackRange);
        var lands = GameState.inbounds(landingCoord)
                 && pieceAt.apply(landingCoord) == null;
        // TODO still have to check if can walk on the landing terrain... but I don't have access to the Board here

        if (!lands) return List.of();

        var list = new ArrayList<Coord>(attackRange - 1);
        var coord = from.moved(dir);
        for (var i = 0; i < attackRange-1; i++) {
            if (!GameState.inbounds(coord)) {
                break;
            }
            var defPiece = pieceAt.apply(coord);
            if (defPiece != null) {
                list.add(coord);
            }
            coord = coord.moved(dir);
        }
        return list;
    }

    @Override
    public boolean attacksFriends() { return true; }

    @Override
    public boolean knockbackOnEqualCombatPower() { return false; }

    @Override
    public int getAttackingPieceDamage(IGameState game, int diff) { return 0; }

    @Override
    public String name() { return "Dash"; }

    @Override
    public MovResult performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game::pieceAt, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            return new MovResult(MovResponse.NO_ATTACKED_PIECE_IN_RANGE);
        }
        var atkPiece = game.pieceAt(atkCoord);

        var landingCoord = atkCoord.moved(atkDirection, attackRange);
        if (!GameState.inbounds(landingCoord) || !atkPiece.machine().type().walksOn(game.board().get(landingCoord))) {
            return new MovResult(MovResponse.ATK_OUT_OF_BOUNDS);
        }
        if (game.pieceAt(landingCoord) != null) {
            return new MovResult(MovResponse.LANDS_ON_NOT_EMPTY);
        }

        var defPieceList = new ArrayList<IPiece>(defCoordList.size());
        for (var defCoord : defCoordList) {
            var defPiece = game.getPiece(defCoord);
            defPieceList.add(defPiece);
            defPiece.setDirection(defPiece.direction().cycle(true).cycle(true)); // Rotate 180??
            var combatPowerDiff = game.getCombatPowerDiff(atkCoord, atkPiece, atkDirection, defCoord);
            game.dealDamage(defCoord, getDefendingPieceDamage(game, combatPowerDiff));
        }

        game.movePiece(atkCoord, landingCoord);

        return new MovResult(landingCoord, defCoordList, defPieceList);
    }

}
