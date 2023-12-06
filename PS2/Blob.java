import java.awt.Graphics;

/**
 * Animated Blob
 * Defined by a position and size, and the ability to step (move/grow), draw itself, and see if a point is inside.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, Fall 2016, Implements Point2D
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */

public class Blob implements Point2D
{
    protected double x, y; // Position
    protected double dx = 0, dy = 0; // Velocity: Defaults to none.
    protected double r = 5; // Radius
    protected double dr = 0; // Growth Step (Size & Sign): Defaults to none.

    public Blob()
    {
        // Do nothing. Everything has its default value.
        // This constructor is implicit unless you provide an alternative.
    }

    /**
     * Constructor
     *
     * @param x Initial x coordinate.
     * @param y Initial y coordinate.
     */
    public Blob(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor
     *
     * @param x Initial x coordinate.
     * @param y Initial y coordinate.
     * @param r Initial radius.
     */
    public Blob(double x, double y, double r)
    {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    // Getters & Setters

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getR()
    {
        return r;
    }

    public void setR(double r)
    {
        this.r = r;
    }

    /**
     * Sets the velocity.
     *
     * @param dx New dx
     * @param dy New dy
     */
    public void setVelocity(double dx, double dy)
    {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Sets the direction of growth.
     *
     * @param dr New dr
     */
    public void setGrowth(double dr)
    {
        this.dr = dr;
    }

    /**
     * Updates the blob (moving & growing).
     */
    public void step()
    {
        x += dx;
        y += dy;
        r += dr;
    }

    /**
     * Tests whether the point (x2, y2) is inside the blob.
     *
     * @return Is (x2,y2) inside the blob?
     */
    public boolean contains(double x2, double y2)
    {
        return (x - x2) * (x - x2) + (y - y2) * (y - y2) <= r * r;
    }

    /**
     * Draws the blob on the graphics.
     */
    public void draw(Graphics g)
    {
        g.fillOval((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
    }
}
