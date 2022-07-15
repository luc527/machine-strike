package logic;

public enum Terrain
{
    CHASM(-2, 'c'),
    MARSH(-1, 'm'),
    GRASSLAND(0, 'g'),
    FOREST(1, 'f'),
    HILL(2, 'h'),
    MOUNTAIN(3, 'H');

    private final int combatPowerOffset;
    private final char repr;

    Terrain(int combatPowerOffset, char repr)
    {
        this.combatPowerOffset = combatPowerOffset;
        this.repr = repr;
    }

    public int combatPowerOffset()
    {
        return this.combatPowerOffset;
    }

    public char toChar()
    {
        return this.repr;
    }

    public static Terrain from(char c)
    {
        switch (c) {
            case 'c': return Terrain.CHASM;
            case 'm': return Terrain.MARSH;
            case 'g': return Terrain.GRASSLAND;
            case 'f': return Terrain.FOREST;
            case 'h': return Terrain.HILL;
            case 'M': return Terrain.MOUNTAIN;
        }
        throw new RuntimeException("Invalid terrain character: " + c);
    }




}
