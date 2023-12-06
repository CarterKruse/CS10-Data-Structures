import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-Based Drawing
 * PS-1, Dartmouth CS 10, Spring 2022
 *
 * @author Chris Bailey-Kellogg, Spring 2015 (Based on a different webcam app from previous terms.)
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class CamPaint extends Webcam
{
    private char displayMode = 'w'; // What To Display: 'w': Live Webcam, 'r': Recolored Image, 'p': Painting
    private RegionFinder finder; // Handles the finding.
    private Color targetColor = null; // Color of regions of interest (set by mouse press).
    private Color paintColor = Color.blue; // The color to put into the painting from the "brush".
    private BufferedImage painting; // The resulting masterpiece.

    /**
     * Initializes the region finder and the drawing.
     */
    public CamPaint()
    {
        finder = new RegionFinder();
        clearPainting();
    }

    /**
     * Resets the painting to a blank image.
     */
    protected void clearPainting()
    {
        painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
     * depending on display variable ('w', 'r', or 'p').
     */
    @Override
    public void draw(Graphics g)
    {
        // Drawing the original image (the webcam) with no processing.
        if (displayMode == 'w')
        {
            g.drawImage(image, 0, 0, null);
        }

        // Drawing the recolored image, which requires a call to the RegionFinder.
        else if (displayMode == 'r' && targetColor != null)
        {
            g.drawImage(finder.getRecoloredImage(), 0, 0, null);
        }

        // Drawing the painting, which is a solid color.
        else if (displayMode == 'p' && targetColor != null)
        {
            g.drawImage(painting, 0, 0, null);
        }

        repaint();
    }

    /**
     * Webcam method, here finding regions and updating the painting.
     */
    @Override
    public void processImage()
    {
        finder.setImage(image);

        // Checking to make sure the displayMode is recolorImage or painting.
        if (displayMode == 'r' || displayMode == 'p')
        {
            // Checking to make sure the image and targetColor are selected (not null).
            if (image != null && targetColor != null)
            {
                // Implementing the findRegions method of RegionFinder.
                finder.findRegions(targetColor);

                // Creating an ArrayList for the largest region.
                ArrayList<Point> largestRegion = finder.largestRegion();

                // Checking to make sure the largest region is not empty.
                if (largestRegion != null)
                {
                    // Recoloring the image, which is live with the webcam.
                    finder.recolorImage();

                    // If the size of the largest region is greater than zero...
                    if (largestRegion.size() > 0)
                    {
                        // Set all the points of the image painting in the largest region to the paintColor.
                        for (Point point : largestRegion)
                        {
                            painting.setRGB(point.x, point.y, paintColor.getRGB());
                        }
                    }
                }
            }
        }

        /* Clearing the regions to ensure that when each frame of the webcam refreshes,
        we do not leave a trail on the recoloredImage. This is essentially how we track
        the movement of a region of pixels that is close enough to a certain color.
         */
        finder.clearRegions();
    }

    /**
     * Overrides the DrawingGUI method to set the track color.
     */
    @Override
    public void handleMousePress(int x, int y)
    {
        if (image != null)
        {
            // Setting the targetColor to the color of the selected pixel.
            targetColor = new Color(image.getRGB(x, y));

            // Changing the displayMode to r to show the recoloredImage.
            displayMode = 'r';
        }

        repaint();
    }

    /**
     * DrawingGUI method, here doing various drawing commands, depending on the key press.
     */
    @Override
    public void handleKeyPress(char k)
    {
        if (k == 'p' || k == 'r' || k == 'w')
        {
            // Display: Painting, Recolored Image, or Webcam
            displayMode = k;
        }

        else if (k == 'c')
        {
            clearPainting();
        }

        else if (k == 'o')
        {
            saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
        }

        else if (k == 's')
        {
            saveImage(painting, "pictures/painting.png", "png");
        }

        else
        {
            // Handling the case of an unexpected key.
            System.out.println("Unexpected Key: " + k);
        }
    }

    // Main method, do not change.
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new CamPaint();
            }
        });
    }
}
