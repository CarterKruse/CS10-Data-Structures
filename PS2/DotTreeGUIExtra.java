import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Driver (Interacting With A Quadtree)
 * Inserting points, viewing the tree, and finding points near a mouse press.
 * Extra Credit Version (Implementation of findInRectangle).
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016
 * @author CBK, Fall 2016
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class DotTreeGUIExtra extends DrawingGUI
{
    private static final int width = 800, height = 600; // Size of the universe.
    private static final int dotRadius = 5; // To draw a dot, so it is visible.

    // To color the different levels differently.
    private static final Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};

    private PointQuadtreeExtra<Dot> tree = null; // Holds the dots.
    private char mode = 'a'; // 'a': Adding; 'q': Querying with the mouse. 'k': Querying with the mouse, rectangle mode.
    private int mouseX, mouseY; // Current mouse location (when querying).
    private int mouseRadius = 10; // Circle around mouse location (for querying).
    private boolean trackMouse = false; // If true, then print out where the mouse is as it moves.
    private List<Dot> found = null; // List of dots near the mouse when querying.

    public DotTreeGUIExtra()
    {
        super("Dot Tree", width, height);
    }

    /**
     * DrawingGUI Method -> Here keeping track of the location and redrawing to show it.
     */
    @Override
    public void handleMouseMotion(int x, int y)
    {
        if (mode == 'q' || mode == 'k')
        {
            mouseX = x;
            mouseY = y;
            repaint();
        }

        if (trackMouse)
        {
            System.out.println(x + ", " + y);
        }
    }

    /**
     * DrawingGUI Method -> Here either adding a new point or querying near the mouse.
     */
    @Override
    public void handleMousePress(int x, int y)
    {
        if (mode == 'a')
        {
            // If the PointQuadtree is not null, we add a dot to the PointQuadtree.
            if (tree != null)
            {
                tree.insert(new Dot(x, y));
            }

            //  If the PointQuadtree does not exist (is null), we initialize a new PointQuadtree using the dot.
            else
            {
                tree = new PointQuadtreeExtra<>(new Dot(x, y), 0, 0, width, height);
            }
        }

        else if (mode == 'q')
        {
            // If the list found it null, we initialize a new ArrayList.
            if (found == null)
            {
                found = new ArrayList<Dot>();
            }

            else
            {
                // Checking to make sure the PointQuadtree is not equal to null.
                if (tree != null)
                {
                    // Setting found to what the tree says is near the mouse press.
                    found = tree.findInCircle(x, y, mouseRadius);
                }
            }
        }

        else if (mode == 'k')
        {
            // If the list found it null, we initialize a new ArrayList.
            if (found == null)
            {
                found = new ArrayList<Dot>();
            }

            else
            {
                // Checking to make sure the PointQuadtree is not equal to null.
                if (tree != null)
                {
                    // Setting found to what the tree says is near the mouse press.
                    found = tree.findInRectangle(x - mouseRadius, y - mouseRadius, x + mouseRadius, y + mouseRadius);
                }
            }
        }

        else
        {
            System.out.println("Clicked At: " + x + ", " + y);
        }

        repaint();
    }

    /**
     * A simple testing procedure, making sure actual is expected, and printing a message if not.
     *
     * @param x Query x coordinate.
     * @param y Query y coordinate.
     * @param r Query circle radius.
     * @param expectedCircleRectangle How many times Geometry.circleIntersectsRectangle is expected to be called.
     * @param expectedInCircle How many times Geometry.pointInCircle is expected to be called.
     * @param expectedHits How many points are expected to be found.
     * @return 0 if passed; 1 if failed
     */
    private int testFind(int x, int y, int r, int expectedCircleRectangle, int expectedInCircle, int expectedHits)
    {
        // Resetting the counter functions for the static Geometry methods.
        Geometry.resetNumInCircleTests();
        Geometry.resetNumCircleRectangleTests();

        // Creating new variables for the number of errors and the size of the found points.
        int errs = 0;
        int num = tree.findInCircle(x, y, r).size();

        String which = "(" + x + ", " + y + ")@" + r;

        // Checking to see if the number of times Geometry.circleIntersectsRectangle is expected to be called matches.
        if (Geometry.getNumCircleRectangleTests() != expectedCircleRectangle)
        {
            errs += 1;
            System.err.println(which + ": Wrong # Circle-Rectangle, Got " + Geometry.getNumCircleRectangleTests() + " but expected " + expectedCircleRectangle);
        }

        // Checking to see if the number of times Geometry.pointInCircle is expected to be called matches.
        if (Geometry.getNumInCircleTests() != expectedInCircle)
        {
            errs += 1;
            System.err.println(which + ": Wrong # In Circle, Got " + Geometry.getNumInCircleTests() + " but expected " + expectedInCircle);
        }

        // Checking to see if the number of points are expected to be found matches.
        if (num != expectedHits)
        {
            errs += 1;
            System.err.println(which + ": Wrong # Hits, Got " + num + " but expected " + expectedHits);
        }

        return errs;
    }

    /**
     * Test Tree 0 -> First three points from figure in handout.
     * Hardcoded point locations for 800x600.
     */
    private void test0()
    {
        found = null;
        tree = new PointQuadtreeExtra<Dot>(new Dot(400, 300), 0, 0, 800, 600); // Start with A.
        tree.insert(new Dot(150, 450)); // B
        tree.insert(new Dot(250, 550)); // C

        int bad = 0;
        bad += testFind(0, 0, 900, 3, 3, 3); // Rectangle for all; circle for all; find all.
        bad += testFind(400, 300, 10, 3, 2, 1); // Rectangle for all; circle for A,B; find A.
        bad += testFind(150, 450, 10, 3, 3, 1); // Rectangle for all; circle for all; find B.
        bad += testFind(250, 550, 10, 3, 3, 1); // Rectangle for all; circle for all; find C.
        bad += testFind(150, 450, 200, 3, 3, 2); // Rectangle for all; circle for all; find B, C.
        bad += testFind(140, 440, 10, 3, 2, 0); // Rectangle for all; circle for A,B; find none.
        bad += testFind(750, 550, 10, 2, 1, 0); // Rectangle for A,B; circle for A; find none.

        if (bad == 0) System.out.println("Test 0 Passed!");
    }

    /**
     * Test Tree 1 -> Figure in handout
     * Hardcoded point locations for 800x600.
     */
    private void test1()
    {
        found = null;
        tree = new PointQuadtreeExtra<Dot>(new Dot(300, 400), 0, 0, 800, 600); // Start with A
        tree.insert(new Dot(150, 450)); // B
        tree.insert(new Dot(250, 550)); // C
        tree.insert(new Dot(450, 200)); // D
        tree.insert(new Dot(200, 250)); // E
        tree.insert(new Dot(350, 175)); // F
        tree.insert(new Dot(500, 125)); // G
        tree.insert(new Dot(475, 250)); // H
        tree.insert(new Dot(525, 225)); // I
        tree.insert(new Dot(490, 215)); // J
        tree.insert(new Dot(700, 550)); // K
        tree.insert(new Dot(310, 410)); // L

        int bad = 0;
        bad += testFind(150, 450, 10, 6, 3, 1); // Rectangle for A [D] [E] [B [C]] [K]; circle for A, B, C; find B.
        bad += testFind(500, 125, 10, 8, 3, 1); // Rectangle for A [D [G F H]] [E] [B] [K]; circle for A, D, G; find G.
        bad += testFind(300, 400, 15, 10, 6, 2); // Rectangle for A [D [G F H]] [E] [B [C]] [K [L]]; circle for A,D,E,B,K,L; find A,L.
        bad += testFind(495, 225, 50, 10, 6, 3); // Rectangle for A [D [G F H [I [J]]]] [E] [B] [K]; circle for A,D,G,H,I,J; find H,I,J
        bad += testFind(0, 0, 900, 12, 12, 12); // Rectangle for all; circle for all; find all.

        if (bad == 0) System.out.println("Test 1 Passed!");
    }

    private void test2()
    {
        found = null;
        tree = new PointQuadtreeExtra<Dot>(new Dot(100, 500), 0, 0, width, height); // start with A
        tree.insert(new Dot(300, 400)); // B
        tree.insert(new Dot(400, 300)); // C
        tree.insert(new Dot(600, 200)); // D
        tree.insert(new Dot(700, 100)); // E

        int bad = 0;
        // Rectangle for all; circle for all; find all.
        bad += testFind(400, 300, 900, 5, 5, 5);

        // Other Test Cases
        bad += testFind(650, 150, 100, 5, 5, 2);
        bad += testFind(70, 500, 70, 3,2,1);
        bad += testFind(250, 300, 50, 4,3,0);
        bad += testFind(400, 200, 20, 5,4,0);
        if (bad == 0) System.out.println("Test 2 Passed!");
    }

    /**
     * DrawingGUI Method -> Here toggling the mode between 'a' and 'q' and increasing/decreasing mouseRadius via +/-.
     */
    @Override
    public void handleKeyPress(char key)
    {
        if (key == 'a' || key == 'q' || key == 'k') mode = key;

        else if (key == '+')
        {
            mouseRadius += 10;
        }

        else if (key == '-')
        {
            mouseRadius -= 10;
            if (mouseRadius < 0) mouseRadius = 0;
        }

        else if (key == 'm')
        {
            trackMouse = !trackMouse;
        }

        else if (key == '0')
        {
            test0();
        }

        else if (key == '1')
        {
            test1();
        }

        else if (key == '2')
        {
            test2();
        }

        repaint();
    }

    /**
     * DrawingGUI Method -> Here drawing the quadtree and if in query mode, the mouse location and any found dots.
     */
    @Override
    public void draw(Graphics g)
    {
        // Drawing the PointQuadtree if it is not null.
        if (tree != null) drawTree(g, tree, 0);

        // If we are querying with the mouse...
        if (mode == 'q')
        {
            // Setting the color of the graphics to black and displaying the circle around the mouse.
            g.setColor(Color.BLACK);
            g.drawOval(mouseX - mouseRadius, mouseY - mouseRadius, 2 * mouseRadius, 2 * mouseRadius);

            // Checking to make sure found it not equal to null.
            if (found != null)
            {
                // Setting the color of the graphics to black.
                g.setColor(Color.BLACK);

                // Cycling through the dots in the list of "found" points.
                for (Dot dot : found)
                {
                    g.fillOval((int) dot.getX() - dotRadius, (int) dot.getY() - dotRadius, 2 * dotRadius, 2 * dotRadius);
                }
            }
        }

        if (mode == 'k')
        {
            // Setting the color of the graphics to black and displaying the circle around the mouse.
            g.setColor(Color.BLACK);
            g.drawRect(mouseX - mouseRadius, mouseY - mouseRadius, mouseRadius * 2 , mouseRadius * 2);

            // Checking to make sure found it not equal to null.
            if (found != null)
            {
                // Setting the color of the graphics to black.
                g.setColor(Color.BLACK);

                // Cycling through the dots in the list of "found" points.
                for (Dot dot : found)
                {
                    g.fillOval((int) dot.getX() - dotRadius, (int) dot.getY() - dotRadius, 2 * dotRadius, 2 * dotRadius);
                }
            }
        }
    }

    /**
     * Draws the dot tree.
     *
     * @param g The graphics object for drawing.
     * @param tree A dot tree (not necessarily root).
     * @param level How far down from the root qt is (0 for root, 1 for its children, etc.)
     */
    public void drawTree(Graphics g, PointQuadtreeExtra<Dot> tree, int level)
    {
        // Setting the color for this level, using a modulus function.
        g.setColor(rainbow[level % rainbow.length]);

        // Drawing the node's dot.
        g.fillOval((int) tree.getPoint().getX() - dotRadius, (int) tree.getPoint().getY() - dotRadius, dotRadius * 2, dotRadius * 2);

        // Drawing the horizontal line.
        g.drawLine(tree.getX1(), (int) tree.getPoint().getY(), tree.getX2(), (int) tree.getPoint().getY());

        // Drawing the vertical line.
        g.drawLine((int) tree.getPoint().getX(), tree.getY1(), (int) tree.getPoint().getX(), tree.getY2());

        // Recurse with the children of the parent node. Drawing all the descendants of the node.
        if (tree.hasChild(1))
        {
            drawTree(g, tree.getChild(1), level + 1);
        }

        if (tree.hasChild(2))
        {
            drawTree(g, tree.getChild(2), level + 1);
        }

        if (tree.hasChild(3))
        {
            drawTree(g, tree.getChild(3), level + 1);
        }

        if (tree.hasChild(4))
        {
            drawTree(g, tree.getChild(4), level + 1);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new DotTreeGUIExtra();
            }
        });
    }
}
