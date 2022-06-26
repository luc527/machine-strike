package logic.turn;

// Could be extended to hold more info, like whether the attack breaks the armor and so on

public class ConflictDamage
{
    public int atkDamage;
    public int defDamage;
    public boolean breakArmor;

    public ConflictDamage(int atkDamage, int defDamage, boolean breakArmor) {
        this.atkDamage = atkDamage;
        this.defDamage = defDamage;
        this.breakArmor = breakArmor;
    }
}
