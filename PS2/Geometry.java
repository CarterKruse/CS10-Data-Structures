/**
 * Geometry Helper Methods
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Fall 2016 (Separated from Quadtree. Instrumented to count calls.)
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class Geometry
{
    private static int numInCircleTests = 0; // Keeps track of how many times pointInCircle() has been called.
    private static int numCircleRectangleTests = 0; // Keeps track of how many times circleIntersectsRectangle() has been called.

    public static int getNumInCircleTests()
    {
        return numInCircleTests;
    }

    public static void resetNumInCircleTests()
    {
        numInCircleTests = 0;
    }

    public static int getNumCircleRectangleTests()
    {
        return numCircleRectangleTests;
    }

    public static void resetNumCircleRectangleTests()
    {
        numCircleRectangleTests = 0;
    }

    /**
     * Returns whether the point is within the circle.
     *
     * @param px Point x coordinate.
     * @param py Point y coordinate.
     * @param cx Circle center x.
     * @param cy Circle center y.
     * @param cr Circle radius.
     */
    public static boolean pointInCircle(double px, double py, double cx, double cy, double cr)
    {
        numInCircleTests += 1;
        return (px - cx) * (px - cx) + (py - cy) * (py - cy) <= cr * cr;
    }

    /**
     * Returns whether the circle intersects the rectangle.
     * Based on discussion at http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
     *
     * @param cx Circle center x.
     * @param cy Circle center y.
     * @param cr Circle radius.
     * @param x1 Rectangle min x.
     * @param y1 Rectangle min y.
     * @param x2 Rectangle max x.
     * @param y2 Rectangle max y.
     */
    public static boolean circleIntersectsRectangle(double cx, double cy, double cr, double x1, double y1, double x2, double y2)
    {
        numCircleRectangleTests += 1;
        double closestX = Math.min(Math.max(cx, x1), x2);
        double closestY = Math.min(Math.max(cy, y1), y2);
        return (cx - closestX) * (cx - closestX) + (cy - closestY) * (cy - closestY) <= cr * cr;
    }
}
