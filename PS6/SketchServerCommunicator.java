import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Sketch Server Communicator - Handles communication between the server and one client.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012, Winter 2014
 * @author Carter Kruse, Dartmouth CS 10, Spring 2022
 */
public class SketchServerCommunicator extends Thread
{
    private Socket socket; // To Talk With Client
    private BufferedReader in; // From Client
    private PrintWriter out; // To Client
    private SketchServer server; // Handling Communication For

    public SketchServerCommunicator(Socket socket, SketchServer server)
    {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Sends a message to the client.
     */
    public void send(String message)
    {
        out.println(message);
    }

    /**
     * Keeps listening for and handling messages from the client.
     */
    public void run()
    {
        try
        {
            System.out.println("Someone Connected");

            // Communication Channel
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Tell the client the current state of the world.
            for (int ID : server.getSketch().IDMap.keySet())
                send("ADD_ID " + ID + " " + server.getSketch().IDMap.get(ID));

            // Keep getting and handling messages from the client.
            String message;
            while ((message = in.readLine()) != null)
            {
                System.out.println("Received: " + message);

                String[] messageParts = message.split(" ");
                if (messageParts.length < 2)
                    System.err.println("Invalid message from client.");

                // The first element of the message parts is the command to use.
                String command = messageParts[0];

                if (command.equals("ADD"))
                    handleAdd(message);

                if (command.equals("MOVE"))
                    handleMove(message);

                if (command.equals("RECOLOR"))
                    handleRecolor(message);

                if (command.equals("DELETE"))
                    handleDelete(message);
            }

            // Clean Up - Note that also remove self from server's list so it doesn't broadcast here.
            server.removeCommunicator(this);
            out.close();
            in.close();
            socket.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function - Adds a given shape in the server sketch and client sketches.
     *
     * @param message The message to pass through the method.
     */
    public synchronized void handleAdd(String message)
    {
        // Ensuring the input is appropriate.
        String[] messageParts = message.split(" ");
        if (messageParts.length < 7)
            System.err.println("Invalid message from client.");

        Shape shape = null;

        // Creating a new shape based on the shape type.
        if (messageParts[1].equals("Ellipse"))
            shape = new Ellipse(Integer.parseInt(messageParts[2]), Integer.parseInt(messageParts[3]),
                    Integer.parseInt(messageParts[4]), Integer.parseInt(messageParts[5]), new Color(Integer.parseInt(messageParts[6])));

        if (messageParts[1].equals("Rectangle"))
            shape = new Rectangle(Integer.parseInt(messageParts[2]), Integer.parseInt(messageParts[3]),
                    Integer.parseInt(messageParts[4]), Integer.parseInt(messageParts[5]), new Color(Integer.parseInt(messageParts[6])));

        if (messageParts[1].equals("Segment"))
            shape = new Segment(Integer.parseInt(messageParts[2]), Integer.parseInt(messageParts[3]),
                    Integer.parseInt(messageParts[4]), Integer.parseInt(messageParts[5]), new Color(Integer.parseInt(messageParts[6])));

        if (messageParts[1].equals("Polyline"))
        {
            shape = new Polyline(Integer.parseInt(messageParts[2]), Integer.parseInt(messageParts[3]), new Color(Integer.parseInt(messageParts[messageParts.length - 1])));

            for (int i = 4; i < messageParts.length - 1; i += 2)
            {
                ((Polyline) shape).addPoint(Integer.parseInt(messageParts[i]), Integer.parseInt(messageParts[i + 1]));
            }
        }

        // Checking to make sure the shape is present.
        if (shape != null)
        {
            // Adding the shape to the server sketch and broadcasting the message.
            server.getSketch().addShape(shape);
            server.broadcast("ADD " + shape);
        }
    }

    /**
     * Helper Function - Moves a given shape in the server sketch and client sketches.
     *
     * @param message The message to pass through the method.
     */
    public synchronized void handleMove(String message)
    {

        String[] messageParts = message.split(" ");
        if (messageParts.length < 4)
            System.err.println("Invalid message from client.");

        // Modifying the server sketch and broadcasting the message.
        server.getSketch().moveShape(Integer.parseInt(messageParts[1]), Integer.parseInt(messageParts[2]), Integer.parseInt(messageParts[3]));
        server.broadcast(message);
    }

    /**
     * Helper Function - Recolors a given shape in the server sketch and client sketches.
     *
     * @param message The message to pass through the method.
     */
    public synchronized void handleRecolor(String message)
    {
        // Ensuring the input is appropriate.
        String[] messageParts = message.split(" ");
        if (messageParts.length < 3)
            System.err.println("Invalid message from client.");

        // Modifying the server sketch and broadcasting the message.
        server.getSketch().recolorShape(Integer.parseInt(messageParts[1]), new Color(Integer.parseInt(messageParts[2])));
        server.broadcast(message);
    }

    /**
     * Helper Function - Deletes a given shape in the server sketch and client sketches.
     *
     * @param message The message to pass through the method.
     */
    public synchronized void handleDelete(String message)
    {
        // Ensuring the input is appropriate.
        String[] messageParts = message.split(" ");
        if (messageParts.length < 2)
            System.err.println("Invalid message from client.");

        // Modifying the server sketch and broadcasting the message.
        server.getSketch().deleteShape(Integer.parseInt(messageParts[1]));
        server.broadcast(message);
    }
}
