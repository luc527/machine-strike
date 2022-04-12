package graphics;


import logic.Piece;

import javax.swing.*;
import java.awt.*;

// :PatternUsed Decorator

public class PieceIcon implements Icon
{
    private final Icon ico;
    private final Piece piece;

    public PieceIcon(Piece piece, Icon ico)
    {
        this.piece = piece;
        this.ico = ico;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        // TODO
        //  imgIcon.paintIcon(c, g, x, y);  // actually, paint it rotated
        //  maybe add another layer of decorator: MachineIcon paints the machine with the points
        //  and PieceIcon decorates it by rotating (according to Direction) and
        //  painting some translucent color on top to differentiate betweenthe player
    }

    @Override
    public int getIconWidth()
    {
        return ico.getIconWidth();
    }

    @Override
    public int getIconHeight()
    {
        return ico.getIconWidth();
    }
}
