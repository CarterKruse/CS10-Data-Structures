/**
 * A blob that wanders a little more resolutely than the one from class,
 * walking in a straight line for a fixed number of steps before switching.
 *
 * Example: Every 10 steps it picks a random new dx and dy,
 * instead of doing that every step like Wanderer.
 *
 * @author Carter Kruse & John Deforest, Dartmouth CS 10, Spring 2022
 *
 */

public class ResoluteWanderer extends Blob
{
    /* The instance variables steps_between_change and current_steps_taken
    are private to ensure they cannot be accessed outside this class.
     */
    private final int steps_between_change; // This variable is final as it is not modified in the class.
    private int current_steps_taken = 0;

    // Constructor that takes initial x and y values and invokes the super constructor.
    public ResoluteWanderer(double x, double y)
    {
        super(x, y);

        // Java Documentation: https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html
        /* Picking a random number of steps between the velocity change using
        Math.random and Math.floor. The Math.floor method necessitates the +1
        to the length of the range of possible numbers.
         */
        steps_between_change = (int) Math.floor(Math.random() * (18 + 1) + 12);
    }

    @Override
    public void step()
    {
        /* When the step function is first called, the velocity will take on
        a random value between -1 and +1 in each of the x and y. The if statement
        ensures that this also happens after the allotted number of steps
        has been reached.
         */
        if (current_steps_taken == 0)
        {
            dx = 2 * (Math.random() - 0.5);
            dy = 2 * (Math.random() - 0.5);
        }

        // Updating the x and y position of the blob, along with the number of steps taken.
        x += dx;
        y += dy;
        current_steps_taken += 1;

        /* This if statement updates the number of steps taken when the number of steps
        exceeds the allotted number of steps between the change in velocity.
         */
        if (current_steps_taken >= steps_between_change)
        {
            current_steps_taken = 0;
        }
    }
}
