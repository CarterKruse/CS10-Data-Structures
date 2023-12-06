import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Testing code for region finding in PS-1.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Winter 2014
 * @author Travis W. Peters, Dartmouth CS 10, Winter 2015
 * @author CBK, Spring 2015, Updated For DrawingGUI
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class RegionsTest extends DrawingGUI
{
    private BufferedImage image;

    /**
     * Test your RegionFinder by passing an image filename and a color to find.
     *
     * @param name The name of the image/window to open.
     * @param finder Implementing the RegionFinder class.
     * @param targetColor Specification of the color to fill.
     */
    public RegionsTest(String name, RegionFinder finder, Color targetColor)
    {
        super(name, finder.getImage().getWidth(), finder.getImage().getHeight());

        // Do the region finding and recolor the image.
        finder.findRegions(targetColor);
        finder.recolorImage();

        // Set the image to the recolored image.
        image = finder.getRecoloredImage();
    }

    @Override
    public void draw(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }

    public static void main(String[] args)
    {
        // Implementing a way to determine how long the code takes to run (in seconds).
        long startTime = System.currentTimeMillis();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new RegionsTest("smiley", new RegionFinder(loadImage("pictures/smiley.png")), new Color(0, 0, 0));
                new RegionsTest("baker", new RegionFinder(loadImage("pictures/baker-640-480.jpg")), new Color(130, 100, 100));
            }
        });

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);

        // Printing out the amount of time the code took to run.
        System.out.println("Duration (Seconds): " + duration / (double) 1000);
    }
}
