import java.util.ArrayList;
import java.util.List;

/**
 * A Point Quadtree
 * Stores an element at a 2D position, with children at the subdivided quadrants.
 * Extra Credit Version
 *
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class PointQuadtreeExtra<E extends Point2D>
{
    private E point; // The point anchoring this node.
    private int x1, y1; // Upper-left corner of the region.
    private int x2, y2; // Bottom-right corner of the region.
    private PointQuadtreeExtra<E> c1, c2, c3, c4; // Children

    /**
     * Constructor
     *
     * Initializes a leaf quadtree, holding the point in the rectangle.
     */
    public PointQuadtreeExtra(E point, int x1, int y1, int x2, int y2)
    {
        this.point = point;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // Getters
    public E getPoint()
    {
        return point;
    }

    public int getX1()
    {
        return x1;
    }

    public int getY1()
    {
        return y1;
    }

    public int getX2()
    {
        return x2;
    }

    public int getY2()
    {
        return y2;
    }

    /**
     * Returns the child (if any) at the given quadrant, 1-4.
     */
    public PointQuadtreeExtra<E> getChild(int quadrant)
    {
        if (quadrant == 1) return c1;
        if (quadrant == 2) return c2;
        if (quadrant == 3) return c3;
        if (quadrant == 4) return c4;
        return null;
    }

    /**
     * Returns whether there is a child at the given quadrant, 1-4.
     */
    public boolean hasChild(int quadrant)
    {
        return (quadrant == 1 && c1 != null) || (quadrant == 2 && c2 != null) || (quadrant == 3 && c3 != null) || (quadrant == 4 && c4 != null);
    }

    /**
     * Inserts the point into the tree.
     *
     * Edge Cases: In situations where the point is at the same x- or y-coordinate as a parent,
     * the quadrant with the lowest number (1-4) is selected for the child.
     */
    public void insert(E point2)
    {
        // Checking to ensure the point is not placed in the same location as the original.
        if (point2.getX() == this.point.getX() && point2.getY() == this.point.getY())
        {
            this.point = point2; // Replacing the original point.
        }

        // Checking to see if the point location is in the first quadrant.
        else if (point2.getX() >= this.point.getX() && point2.getY() <= this.point.getY())
        {
            // If the first quadrant already has a child, recursively add the point.
            if (this.hasChild(1))
            {
                this.c1.insert(point2);
            }

            // Otherwise, create a new PointQuadTree with the appropriate node and bounds for the rectangle.
            else
            {
                this.c1 = new PointQuadtreeExtra<>(point2, (int) this.point.getX(), this.y1, this.x2, (int) this.point.getY());
            }
        }

        // Checking to see if the point location is in the second quadrant.
        else if (point2.getX() <= this.point.getX() && point2.getY() <= this.point.getY())
        {
            // If the second quadrant already has a child, recursively add the point.
            if (this.hasChild(2))
            {
                this.c2.insert(point2);
            }

            // Otherwise, create a new PointQuadTree with the appropriate node and bounds for the rectangle.
            else
            {
                this.c2 = new PointQuadtreeExtra<>(point2, this.x1, this.y1, (int) this.point.getX(), (int) this.point.getY());
            }
        }

        // Checking to see if the point location is in the third quadrant.
        else if (point2.getX() <= this.point.getX() && point2.getY() >= this.point.getY())
        {
            // If the third quadrant already has a child, recursively add the point.
            if (this.hasChild(3))
            {
                this.c3.insert(point2);
            }

            // Otherwise, create a new PointQuadTree with the appropriate node and bounds for the rectangle.
            else
            {
                this.c3 = new PointQuadtreeExtra<>(point2, this.x1, (int) this.point.getY(), (int) this.point.getX(), this.y2);
            }
        }

        // Checking to see if the point location is in the fourth quadrant.
        else if (point2.getX() >= this.point.getX() && point2.getY() >= this.point.getY())
        {
            // If the fourth quadrant already has a child, recursively add the point.
            if (this.hasChild(4))
            {
                this.c4.insert(point2);
            }

            // Otherwise, create a new PointQuadTree with the appropriate node and bounds for the rectangle.
            else
            {
                this.c4 = new PointQuadtreeExtra<>(point2, (int) this.point.getX(), (int) this.point.getY(), this.x2, this.y2);
            }
        }
    }

    /**
     * Finds the number of points in the quadtree (including its descendants).
     */
    public int size()
    {
        // The count starts at one, as we consider the node of the quadtree.
        int count = 1;

        // Checking through each quadrant for children and recursively returning the size.
        if (this.hasChild(1)) count += this.c1.size();
        if (this.hasChild(2)) count += this.c2.size();
        if (this.hasChild(3)) count += this.c3.size();
        if (this.hasChild(4)) count += this.c4.size();

        return count;
    }

    /**
     * Builds a list of all the points in the quadtree (including its descendants).
     *
     * @return The points in the quadtree.
     */
    public List<E> allPoints()
    {
        // Initializing the list of points in the quadtree.
        List<E> pointsList = new ArrayList<>();

        // Helper method adds each point in the quadtree to the list, recursively.
        addPoints(pointsList);
        return pointsList;
    }

    /**
     * Uses the quadtree to find all points within the circle.
     *
     * @param cx Circle center x.
     * @param cy Circle center y.
     * @param cr Circle radius.
     *
     * @return The points in the circle.
     */
    public List<E> findInCircle(double cx, double cy, double cr)
    {
        // Initializing the list of the points in the circle.
        List<E> pointsInCircle = new ArrayList<>();

        // Helper method adds each point in the circle to the list, recursively.
        addPointsInCircle(pointsInCircle, cx, cy, cr);
        return pointsInCircle;
    }

    /**
     * Uses the quadtree to find all points within the rectangle.
     *
     * @param rx1 Top left x coordinate of rectangle.
     * @param ry1 Top left y coordinate of rectangle.
     * @param rx2 Bottom right x coordinate of rectangle.
     * @param ry2 Bottom right y coordinate of rectangle.
     *
     * @return The points in the rectangle.
     */
    public List<E> findInRectangle(double rx1, double ry1, double rx2, double ry2)
    {
        // Initializing the list of the points in the rectangle.
        List<E> pointsInRectangle = new ArrayList<>();

        // Helper method adds each point in the rectangle to the list, recursively.
        addPointsInRectangle(pointsInRectangle, rx1, ry1, rx2, ry2);
        return pointsInRectangle;
    }

    // Helper Methods

    /**
     * Accumulator -> Adds all points in the quadtree to a list, recursively.
     *
     * @param pointsList The list of points to consider when adding points to the list of all points.
     */
    public void addPoints(List<E> pointsList)
    {
        // Start by adding the point which is the node.
        pointsList.add(this.point);

        // Recursively pass the points which are the children of the node to the method.
        if (this.hasChild(1))
        {
            this.c1.addPoints(pointsList);
        }

        if (this.hasChild(2))
        {
            this.c2.addPoints(pointsList);
        }

        if (this.hasChild(3))
        {
            this.c3.addPoints(pointsList);
        }

        if (this.hasChild(4))
        {
            this.c4.addPoints(pointsList);
        }
    }

    /**
     * Accumulator -> Adds all points in the circle to a list, recursively.
     *
     * @param pointsInCircleList The list of points to consider when adding points to the list.
     * @param cx The x-coordinate of the center of the circle.
     * @param cy The y-coordinate of the center of the circle.
     * @param cr The radius of the circle.
     */
    public void addPointsInCircle(List<E> pointsInCircleList, double cx, double cy, double cr)
    {
        // Using the Geometry helper method to determine if the circle intersects a given quadrant.
        if (Geometry.circleIntersectsRectangle(cx, cy, cr, this.x1, this.y1, this.x2, this.y2))
        {
            // Using the Geometry helper method to determine if the point is within the circle.
            if (Geometry.pointInCircle(this.point.getX(), this.point.getY(), cx, cy, cr))
            {
                // Adding the point which is within the circle.
                pointsInCircleList.add(this.point);
            }

            // Recursively pass the points which are the children of the node to the method.
            if (hasChild(1))
            {
                this.c1.addPointsInCircle(pointsInCircleList, cx, cy, cr);
            }

            if (hasChild(2))
            {
                this.c2.addPointsInCircle(pointsInCircleList, cx, cy, cr);
            }

            if (hasChild(3))
            {
                this.c3.addPointsInCircle(pointsInCircleList, cx, cy, cr);
            }

            if (hasChild(4))
            {
                this.c4.addPointsInCircle(pointsInCircleList, cx, cy, cr);
            }
        }
    }

    /**
     * Accumulator -> Adds all points in the rectangle to a list, recursively.
     *
     * @param pointsInRectangleList The list of points to consider when adding points to the list.
     * @param rx1 Top left x coordinate of rectangle.
     * @param ry1 Top left y coordinate of rectangle.
     * @param rx2 Bottom right x coordinate of rectangle.
     * @param ry2 Bottom right y coordinate of rectangle.
     */
    public void addPointsInRectangle(List<E> pointsInRectangleList, double rx1, double ry1, double rx2, double ry2)
    {
        // Using the Geometry helper method to determine if the point is within the rectangle.
        if (GeometryExtra.pointInRectangle(this.point.getX(), this.point.getY(), rx1, ry1, rx2, ry2))
        {
            // Adding the point which is within the rectangle.
            pointsInRectangleList.add(this.point);
        }

        // Recursively pass the points which are the children of the node to the method.
        if (hasChild(1))
        {
            this.c1.addPointsInRectangle(pointsInRectangleList, rx1, ry1, rx2, ry2);
        }

        if (hasChild(2))
        {
            this.c2.addPointsInRectangle(pointsInRectangleList, rx1, ry1, rx2, ry2);
        }

        if (hasChild(3))
        {
            this.c3.addPointsInRectangle(pointsInRectangleList, rx1, ry1, rx2, ry2);
        }

        if (hasChild(4))
        {
            this.c4.addPointsInRectangle(pointsInRectangleList, rx1, ry1, rx2, ry2);
        }
    }
}
