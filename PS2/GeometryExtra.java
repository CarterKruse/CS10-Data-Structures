/**
 * Geometry Helper Methods
 * Implementation of pointInRectangle method.
 * Extra Credit Version
 *
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class GeometryExtra extends Geometry
{
    /**
     * Returns whether the point is within the rectangle.
     *
     * @param px Point x coordinate.
     * @param py Point y coordinate.
     * @param rx1 Top left x coordinate of rectangle.
     * @param ry1 Top left y coordinate of rectangle.
     * @param rx2 Bottom right x coordinate of rectangle.
     * @param ry2 Bottom right y coordinate of rectangle.
     */
    public static boolean pointInRectangle(double px, double py, double rx1, double ry1, double rx2, double ry2)
    {
        return (px > rx1 && px < rx2) && (py > ry1 && py < ry2);
    }
}
