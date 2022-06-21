package logic;

// Could be extended to hold more info, like whether the attack breaks the armor and so on

public class ConflictDamage
{
    public int attackerDmg;
    public int attackedDmg;

    public ConflictDamage(int attackerDmg, int attackedDmg) {
        this.attackerDmg = attackerDmg;
        this.attackedDmg = attackedDmg;
    }
}
