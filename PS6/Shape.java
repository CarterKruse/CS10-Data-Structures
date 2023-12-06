import java.awt.Color;
import java.awt.Graphics;

/**
 * Shape
 * A geometric entity with a color.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public interface Shape
{
    /**
     * Move By - Moves the shape by dx in the x coordinate and dy in the y coordinate.
     *
     * @param dx The amount to move in the x direction.
     * @param dy The amount to move in the y direction.
     */
    public void moveBy(int dx, int dy);

    /**
     * Contains - Whether the point is inside the shape.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public boolean contains(int x, int y);

    /**
     * Get Color
     *
     * @return The shape's color.
     */
    public Color getColor();

    /**
     * Set Color
     *
     * @param color The shape's color.
     */
    public void setColor(Color color);

    /**
     * Draws the shape.
     *
     * @param g The graphics to use.
     */
    public void draw(Graphics g);
}
