package gamebuild;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PiecePlacementView //implements PiecePlacementObserver
{
    private GameBuildingController controller;
    private JFrame frame;

    public PiecePlacementView(GameBuildingController controller)
    {
        this.controller = controller;

        //controller.attach(this);

        frame = new JFrame();
        var panel = new JPanel();
        frame.setContentPane(panel);


        panel.add(new JLabel("hello, world!"));
    }

    public void show()
    {
        frame.pack();
        frame.setVisible(true);
    }

    public void onClose(Runnable r)
    {
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                r.run();
                frame.dispose();
            }
        });
    }
}
