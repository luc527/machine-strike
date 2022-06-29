package logic.turn;

// Could be extended to hold more info, like whether the attack breaks the armor and so on

public record ConflictResult(int atkDamage, int defDamage, boolean knockback)
{
}
