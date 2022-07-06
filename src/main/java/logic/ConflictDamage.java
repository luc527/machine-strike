package logic;

// Could be extended to hold more info, like whether the attack breaks the armor and so on

public record ConflictDamage(int atkDamage, int defDamage, boolean knockback)
{
}
