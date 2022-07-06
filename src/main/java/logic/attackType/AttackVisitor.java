package logic.attackType;

import logic.Coord;
import logic.IPiece;

import java.util.Optional;

public interface AttackVisitor
{
    void visitAttack(Coord coord, Optional<IPiece> piece);
}
