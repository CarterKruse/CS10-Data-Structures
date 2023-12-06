import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016
 * @author CBK, Fall 2016
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 */
public class CollisionGUI extends DrawingGUI
{
    private static final int width = 800, height = 600; // Size of the universe.

    private List<Blob> blobs; // List of all the blobs.
    private List<Blob> colliders; // List of the blobs who collided at this step.
    private char blobType = 'b'; // What type of blob to create.
    private char collisionHandler = 'c'; // When there is a collision, color (c) or destroy (d) them.
    private int delay = 100; // Timer Control

    public CollisionGUI()
    {
        super("Super Collider", width, height);

        blobs = new ArrayList<Blob>();

        // Timer drives the animation.
        startTimer();
    }

    /**
     * Adds a blob of the current blobType at the location.
     */
    private void add(int x, int y)
    {
        if (blobType == 'b')
        {
            blobs.add(new Bouncer(x, y, width, height));
        }

        else if (blobType == 'w')
        {
            blobs.add(new Wanderer(x, y));
        }

        else
        {
            System.err.println("Unknown Blob Type: " + blobType);
        }
    }

    /**
     * DrawingGUI Method -> Here creating a new blob.
     */
    public void handleMousePress(int x, int y)
    {
        add(x, y);
        repaint();
    }

    /**
     * DrawingGUI Method
     */
    public void handleKeyPress(char k)
    {
        if (k == 'f') // Faster
        {
            if (delay > 1) delay /= 2;
            setTimerDelay(delay);
            System.out.println("Delay: " + delay);
        }

        else if (k == 's') // Slower
        {
            delay *= 2;
            setTimerDelay(delay);
            System.out.println("Delay: " + delay);
        }

        else if (k == 'r') // Add 10 new blobs at random positions.
        {
            // Cycling through for loop (10 iterations).
            for (int i = 0; i < 10; i += 1)
            {
                add((int) (width * Math.random()), (int) (height * Math.random()));
                repaint();
            }
        }

        else if (k == 'c' || k == 'd') // Control how the collisions are handled.
        {
            collisionHandler = k;
            System.out.println("Collision: " + k);
        }

        else if (k == 't') // Using a keystroke for testing of collisions.
        {
            collisionDetectionTest();
        }

        else // Set the type for new blobs.
        {
            blobType = k;
        }
    }

    /**
     * DrawingGUI Method -> Here drawing all the blobs and then re-drawing the colliders in red.
     */
    public void draw(Graphics g)
    {
        // Checking to ensure there are blobs to draw.
        if (this.blobs != null)
        {
            // Cycling through each blob in the list.
            for (Blob blob : this.blobs)
            {
                g.setColor(Color.black); // Blobs are assigned the color black.
                blob.draw(g);
            }
        }

        // Checking to ensure there are colliders to draw.
        if (this.colliders != null)
        {
            // Cycling through each collider in the list.
            for (Blob collider : colliders)
            {
                g.setColor(Color.red); // Colliders are assigned the color red.
                collider.draw(g); // Re-drawing the colliders over the blobs.
            }
        }
    }

    /**
     * Sets colliders to include all blobs in contact with another blob.
     */
    private void findColliders()
    {
        // Creating an empty tree. The node will initialize the tree.
        PointQuadtree<Blob> treeOfBlobs = null;

        // Filling the PointQuadtree with all the blobs.

        // Checking to make sure the list of blobs is not null.
        if (this.blobs != null)
        {
            // Cycling through all the blobs in the list.
            for (Blob blob : this.blobs)
            {
                // If the PointQuadtree does not have any blobs, then create a new PointQuadtree with the node.
                if (treeOfBlobs == null)
                {
                    treeOfBlobs = new PointQuadtree<Blob>(blob, 0, 0, width, height);
                }

                // Otherwise, insert the blob into the PointQuadtree.
                else
                {
                    treeOfBlobs.insert(blob);
                }
            }
        }

        // Identifying the colliders based on the findInCircle() method.

        // Checking to ensure that the PointQuadtree is not null.
        if (treeOfBlobs != null)
        {
            // If the list of colliders is null, then we create a new ArrayList of type Blob.
            if (colliders == null)
            {
                colliders = new ArrayList<Blob>();
            }

            // Cycling through all the blobs in the list.
            for (Blob blob : blobs)
            {
                /* For each blob, we check to see if any other blob collided with it.
                The value of 1 is used, as we already know the initial blob is in the circle.
                We use 2 * [radius], as this detects a collision on the edges of the blobs.
                 */
                if (treeOfBlobs.findInCircle(blob.x, blob.y, blob.r * 2).size() > 1)
                {
                    colliders.add(blob); // If so, we add this blob to the list of colliders.
                }
            }
        }
    }

    /**
     * DrawingGUI Method -> Here moving all the blobs and checking for collisions.
     */
    public void handleTimer()
    {
        // Ask all the blobs to move themselves.
        for (Blob blob : blobs)
        {
            blob.step();
        }

        // Check for collisions.
        if (blobs.size() > 0)
        {
            // Ensuring that the colliders do not remain red after the collision has occurred.
            colliders = null;

            findColliders();

            // Checking to see whether to destroy (d) the colliders.
            if (collisionHandler == 'd')
            {
                blobs.removeAll(colliders); // Removing the colliders from the list of blobs.
                colliders = null; // Setting the list of colliders to null.
            }
        }

        // Now update the drawing.
        repaint();
    }

    /**
     * Method used to carefully test cases of things that should collide and things that should not.
     */
    public void collisionDetectionTest()
    {
        // Clearing all the blob objects currently on the screen.
        blobs = new ArrayList<>();

        // Setting the blobType to "Bouncer".
        blobType = 'b';

        // Creating new blob objects that are placed exactly far enough way such that there should be a collision.
        Blob bouncer1 = new Bouncer(200, 100, width, height);
        Blob bouncer2 = new Bouncer(400, 120, width, height);
        bouncer1.setR(10);
        bouncer2.setR(10);

        // Setting the blobs on parallel paths of intersection.
        bouncer1.setVelocity(5, 0);
        bouncer2.setVelocity(-5, 0);

        // Adding the blob objects to the array of blobs to be displayed in the GUI.
        blobs.add(bouncer1);
        blobs.add(bouncer2);

        // Creating new blob objects that are placed far enough way such that there should NOT be a collision.
        Blob bouncer3 = new Bouncer(200, 300, width, height);
        Blob bouncer4 = new Bouncer(400, 330, width, height);
        bouncer3.setR(10);
        bouncer4.setR(10);

        // Setting the blobs on parallel paths.
        bouncer3.setVelocity(5, 0);
        bouncer4.setVelocity(-5, 0);

        // Adding the blob objects to the array of blobs to be displayed in the GUI.
        blobs.add(bouncer3);
        blobs.add(bouncer4);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new CollisionGUI();
            }
        });
    }
}
