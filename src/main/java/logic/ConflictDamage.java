package logic;

// Could be extended to hold more info, like whether the attack breaks the armor and so on

public class ConflictDamage
{
    public int atkDamage;
    public int defDamage;

    public ConflictDamage(int atkDamage, int defDamage) {
        this.atkDamage = atkDamage;
        this.defDamage = defDamage;
    }
}
