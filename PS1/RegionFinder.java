import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Region Growing Algorithm: Finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Winter 2014
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, Updated For CamPaint
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class RegionFinder
{
    /* How similar a pixel color must be to the target color to belong to a region.
    This value is significantly different from what was provided because of the way
    we chose to determine the difference in colors. */
    private static final int maxColorDiff = 1000;

    // How many points must be in a region to be worth considering.
    private static final int minRegion = 50;

    private BufferedImage image; // The image in which to find regions.
    private BufferedImage recoloredImage; // The image with identified regions recolored.
    private BufferedImage visited; // The image which changes from black to white as pixels are visited.

    // A region is a list of points, so the identified regions are a list of lists of points.
    private ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();

    // Constructor with no arguments results in the image being set to null.
    public RegionFinder()
    {
        this.image = null;
    }

    // Constructor with an argument that is the BufferedImage.
    public RegionFinder(BufferedImage image)
    {
        this.image = image;
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public BufferedImage getRecoloredImage()
    {
        return recoloredImage;
    }

    /**
     * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
     */
    public void findRegions(Color targetColor)
    {
        // Creating a new visited BufferedImage to keep track of the points we have visited, so we don't revisit them.
        visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Cycling through every pixel of the original image.
        for (int x = 0; x < image.getWidth(); x += 1)
        {
            for (int y = 0; y < image.getHeight(); y += 1)
            {
                // Creating a new Color object for every pixel.
                Color c = new Color(image.getRGB(x, y));

                // Checking to see if the pixel has already been visited and if it matches the target color.
                if (visited.getRGB(x, y) == 0 && colorMatch(c, targetColor))
                {
                    // Creating new ArrayLists of the points in a given region and the points still to visit.
                    ArrayList<Point> regionPoints = new ArrayList<Point>();
                    ArrayList<Point> toVisit = new ArrayList<Point>();
                    toVisit.add(new Point(x, y));

                    // The while loop checks to see if there are still any points we should visit and check.
                    while (!toVisit.isEmpty())
                    {
                        // Popping off the first element of the toVisit ArrayList and removing it.
                        Point pointToVisit = toVisit.get(0);
                        toVisit.remove(0);

                        // Creating a new Color object for the point to visit as a way to compare it to the target color.
                        Color d = new Color(image.getRGB(pointToVisit.x, pointToVisit.y));

                        // Checking to see if the pixel has already been visited and if it matches the target color.
                        if (visited.getRGB(pointToVisit.x, pointToVisit.y) == 0 && colorMatch(d, targetColor))
                        {
                            // Adding the point to the ArrayList regionPoints, which keeps track of all points in a given region.
                            regionPoints.add(pointToVisit);

                            // Setting the visited BufferedImage RGB value to 1 so that we do not revisit the point.
                            visited.setRGB(pointToVisit.x, pointToVisit.y, 1);

                            /* Adding the four surrounding points to the toVisit ArrayList.
                            This is how the flood-fill algorithm functions, neighboring points are added
                            and then checked to see if they are close enough to the target color we aim for.
                             */
                            toVisit.add(new Point(constrain(pointToVisit.x - 1, 0, image.getWidth() - 1), pointToVisit.y));
                            toVisit.add(new Point(pointToVisit.x, constrain(pointToVisit.y - 1, 0, image.getHeight() - 1)));
                            toVisit.add(new Point(constrain(pointToVisit.x + 1, 0, image.getWidth() - 1), pointToVisit.y));
                            toVisit.add(new Point(pointToVisit.x, constrain(pointToVisit.y + 1, 0, image.getHeight() - 1)));
                        }
                    }

                    /* If the size of the region is greater than the specified minimum size,
                    add the region to the list of regions.
                     */
                    if (regionPoints.size() >= minRegion)
                    {
                        regions.add(regionPoints);
                    }
                }
            }
        }
    }

    // The constrain function is used to ensure we do not add a point to the toVisit ArrayList that is not on the screen.
    public int constrain(int value, int min, int max)
    {
        return Math.max(Math.min(value, max), min);
    }

    /**
     * Tests whether the two colors are "similar enough".
     * The definition is subject to the maxColorDiff threshold, which may vary.
     */
    private static boolean colorMatch(Color c1, Color c2)
    {
        // Euclidean distance squared between colors.
        int colorDiff = (int) (Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2)
                + Math.pow(c1.getBlue() - c2.getBlue(), 2));

        // Returning the boolean value (true/false) of whether the color difference is close enough.
        return colorDiff <= maxColorDiff;
    }

    /**
     * Returns the largest region detected (if any region has been detected).
     */
    public ArrayList<Point> largestRegion()
    {
        // Creating a new ArrayList to hold the points of the largest region.
        ArrayList<Point> largestRegion = new ArrayList<Point>();
        int sizeOfLargestRegion = 0;

        // For each ArrayList region in the ArrayList regions...
        for (ArrayList<Point> region : regions)
        {
            // Detecting if the size of the region is greater than the size of the largest region.
            if (region.size() > sizeOfLargestRegion)
            {
                // If it is, update the size of the largest region and set the region to be the largest region.
                sizeOfLargestRegion = region.size();
                largestRegion = region;
            }
        }

        return largestRegion;
    }

    /**
     * Sets recoloredImage to be a copy of image, but with each region a uniform random color,
     * so we can see where they are.
     */
    public void recolorImage()
    {
        // Creating a copy of the original image.
        recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
                image.getColorModel().isAlphaPremultiplied(), null);

        // Recoloring the regions within the image.
        for (ArrayList<Point> region : regions)
        {
            // Creating a random color by setting random RGB values.
            Color randomColor = new Color((int) (Math.random() * 256),
                    (int) (Math.random() * 256),
                    (int) (Math.random() * 256));

            // For every point within the region, set the RGB value of the recoloredImage at that point to the random color.
            for (Point point : region)
            {
                recoloredImage.setRGB(point.x, point.y, randomColor.getRGB());
            }
        }
    }

    /**
     * Removes every region from the list of regions, which proves useful
     * when trying to ensure that the CamPaint program does not leave a trail
     * when recoloring the webcam image.
     */
    public void clearRegions()
    {
        // Cycling through the ArrayList regions and removing every element.
        for (int i = 0; i < regions.size(); )
            regions.remove(i);
    }
}
