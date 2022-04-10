package logic;

public enum Terrain
{
    CHASM(-2),
    MARSH(-1),
    GRASSLAND(0),
    FOREST(1),
    HILL(2),
    MOUNTAIN(3);

    private int combatPowerOffset;

    Terrain(int combatPowerOffset)
    {
        this.combatPowerOffset = combatPowerOffset;
    }

    public int combatPowerOffset()
    {
        return this.combatPowerOffset;
    }
}
