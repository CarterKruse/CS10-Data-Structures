import java.awt.Color;
import java.awt.Graphics;

/**
 * An Ellipse-Shaped Shape
 * Defined by an upper-left corner (x1, y1) and a lower-right corner (x2, y2) with x1 <= x2 and y1 <= y2.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Fall 2016
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class Ellipse implements Shape
{
    private int x1, y1, x2, y2; // Upper left and lower right.
    private Color color;

    /**
     * An "empty" ellipse, with only one point set so far, with a color.
     *
     * @param x1    The x coordinate of the point.
     * @param y1    The y coordinate of the point.
     * @param color The color of the Ellipse.
     */
    public Ellipse(int x1, int y1, Color color)
    {
        this.x1 = x1;
        this.x2 = x1;
        this.y1 = y1;
        this.y2 = y1;
        this.color = color;
    }

    /**
     * An ellipse defined by two corners and a color.
     *
     * @param x1    The x coordinate of one of the corners.
     * @param y1    The y coordinate of one of the corners.
     * @param x2    The x coordinate of one of the corners.
     * @param y2    The y coordinate of one of the corners.
     * @param color The color of the Ellipse.
     */
    public Ellipse(int x1, int y1, int x2, int y2, Color color)
    {
        setCorners(x1, y1, x2, y2);
        this.color = color;
    }

    /**
     * Redefines the ellipse based on new corners.
     *
     * @param x1 The x coordinate of one of the corners.
     * @param y1 The y coordinate of one of the corners.
     * @param x2 The x coordinate of one of the corners.
     * @param y2 The y coordinate of one of the corners.
     */
    public void setCorners(int x1, int y1, int x2, int y2)
    {
        // Ensure correct upper left and lower right.
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
    }

    @Override
    public void moveBy(int dx, int dy)
    {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    @Override
    public boolean contains(int x, int y)
    {
        double a = (x2 - x1) / 2.0, b = (y2 - y1) / 2.0;
        double dx = x - (x1 + a); // Horizontal distance from center.
        double dy = y - (y1 + b); // Vertical distance from center.

        // Apply the standard geometry formula.
        return Math.pow(dx / a, 2) + Math.pow(dy / b, 2) <= 1;
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    @Override
    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public void draw(Graphics g)
    {
        g.setColor(color);
        g.fillOval(x1, y1, x2 - x1, y2 - y1);
    }

    @Override
    public String toString()
    {
        return "Ellipse " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + color.getRGB();
    }
}
