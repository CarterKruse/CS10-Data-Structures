import java.awt.*;
import java.util.*;

/**
 * A class used to maintain sketches (the shapes shared among the editors).
 * Stores a list of the shapes used by various server clients.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */

public class Sketch
{
    // Instance Variables - IDMap to hold the IDs and corresponding shapes, and the current IDIndex.
    TreeMap<Integer, Shape> IDMap = new TreeMap<>();
    int IDIndex;

    public Sketch()
    {
        IDIndex = 0;
    }

    /**
     * Determines which shape is clicked by its ID.
     *
     * @param p The point to consider.
     */
    public int IDFromClicked(Point p)
    {
        // Cycling through all the IDs in the IDMap.
        for (int ID : IDMap.keySet())
        {
            // If the IDMap shape contains the point, return the ID considered.
            if (IDMap.get(ID).contains(p.x, p.y))
                return ID;
        }

        // Else, return -1 to indicate that no shape was found.
        return -1;
    }

    /**
     * Adds a shape to the map (and to the editor).
     *
     * @param shape The shape to add.
     */
    public synchronized void addShape(Shape shape)
    {
        // Inputting the shape into the IDMap.
        IDMap.put(IDIndex, shape);
        IDIndex += 1;
    }

    /**
     * Adds a shape to the map (and to the editor).
     *
     * @param ID The ID of the shape to add.
     * @param shape The shape to add.
     */
    public synchronized void addShape(int ID, Shape shape)
    {
        // Inputting the shape into the ID Map.
        IDMap.put(ID, shape);
        IDIndex += 1;
    }

    /**
     * Move Shape - Moves a shape by dx and dy, depending on its ID.
     *
     * @param ID The ID of the shape to move.
     * @param dx The x coordinate of movement.
     * @param dy The y coordinate of movement.
     */
    public synchronized void moveShape(int ID, int dx, int dy)
    {
        if (IDMap.containsKey(ID))
            IDMap.get(ID).moveBy(dx, dy);
    }

    /**
     * Recolor Shape - Recolors a shape, depending on its ID.
     *
     * @param ID The ID of the shape to move.
     * @param color The color to use when recoloring the shape.
     */
    public synchronized void recolorShape(int ID, Color color)
    {
        if (IDMap.containsKey(ID))
            IDMap.get(ID).setColor(color);
    }

    /**
     * Deletes a shape from the list (and the editor), determined by ID.
     *
     * @param ID The ID of the shape to delete.
     */
    public synchronized void deleteShape(int ID)
    {
        IDMap.remove(ID);
    }
}
