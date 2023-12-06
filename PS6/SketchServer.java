import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Sketch Server - Used to handle sketches: getting requests from the clients, updating the overall state, and passing
 * them on to the clients.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class SketchServer
{
    private ServerSocket listen; // For Accepting Connections
    private ArrayList<SketchServerCommunicator> communicators; // All the connections with clients.
    private Sketch sketch; // The state of the world.

    public SketchServer(ServerSocket listen)
    {
        this.listen = listen;
        sketch = new Sketch();
        communicators = new ArrayList<>();
    }

    public Sketch getSketch()
    {
        return sketch;
    }

    /**
     * Get Connections - The usual loop of accepting connections and firing off new threads to handle them.
     */
    public void getConnections() throws IOException
    {
        System.out.println("Server ready for connections.");

        while (true)
        {
            SketchServerCommunicator communicator = new SketchServerCommunicator(listen.accept(), this);
            communicator.setDaemon(true);
            communicator.start();
            addCommunicator(communicator);
        }
    }

    /**
     * Adds the communicator to the list of current communicators.
     */
    public synchronized void addCommunicator(SketchServerCommunicator communicator)
    {
        communicators.add(communicator);
    }

    /**
     * Removes the communicator from the list of current communicators.
     */
    public synchronized void removeCommunicator(SketchServerCommunicator communicator)
    {
        communicators.remove(communicator);
    }

    /**
     * Sends the message from the one communicator to all (including the originator).
     */
    public synchronized void broadcast(String msg)
    {
        for (SketchServerCommunicator communicator : communicators)
        {
            communicator.send(msg);
        }
    }

    public static void main(String[] args) throws Exception
    {
        new SketchServer(new ServerSocket(4242)).getConnections();
    }
}
