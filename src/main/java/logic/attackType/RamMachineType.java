package logic.attackType;

import logic.*;

import java.util.List;

public class RamMachineType extends MachineType
{
    public RamMachineType(int attackRange)
    { super(attackRange); }

    @Override
    public List<Coord> attackedCoords(IGameState game, Coord from, IPiece piece, Direction dir)
    { return firstInAttackRange(game, from, piece, dir); }

    @Override
    public String toString()
    { return "MeeleeAttack("+attackRange+")"; }

    @Override
    public boolean knockbackOnEqualCombatPower()
    { return false; }

    @Override
    public MovResponse performAttack(GameState game, Coord atkCoord, Direction atkDirection)
    {
        var defCoordList = attackedCoords(game, atkCoord, atkDirection);
        if (defCoordList.isEmpty()) {
            // See MeleeMachineType
            return MovResponse.NO_ATTACKED_PIECE_IN_RANGE;
        }
        var defCoord = defCoordList.get(0);

        var res = performBasicAttack(game, atkCoord, atkDirection, defCoord);
        if (res != MovResponse.OK) return res;

        // This is where the Ram attack type differs: there's always knockback
        // and the attacking piece moves onto the square left by the defending piece
        var defPiece = game.pieceAt(defCoord);

        var defKnocked = false;
        if (!defPiece.dead()) {
            var knockCoord = defCoord.moved(atkDirection);
            if (GameState.inbounds(knockCoord)) {
                game.movePiece(defCoord, knockCoord);
                defKnocked = true;
            }
        }

        var atkPiece = game.pieceAt(atkCoord);
        attackerFinalPosition = atkCoord;
        if (!atkPiece.dead() && defKnocked) {
            game.movePiece(atkCoord, defCoord);
            attackerFinalPosition = defCoord;
        }

        return MovResponse.OK;
    }

    @Override
    public String name() { return "Ram"; } }
