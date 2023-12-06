import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * EchoServer - A simple server which accepts a connection and simply reads input and echos it back to the sender.
 * The code is provided to enable testing of (1) sending/receiving messages from the server, and (2) updating a sketch
 * based on messages.
 *
 * @author Travis Peters, Dartmouth CS 10, Winter 2015
 * @author Carter Kruse, Dartmouth CS 10, Spring 2022
 */
public class EchoServer
{
    private ServerSocket listen; // For Accepting Connections

    public EchoServer(ServerSocket listen)
    {
        this.listen = listen;
    }

    ///////////////////////////////////////////////////////////////////////

    private class EchoServerCommunicator extends Thread
    {
        private Socket socket;
        private BufferedReader in; // From Client
        private PrintWriter out; // To Client

        public EchoServerCommunicator(Socket socket)
        {
            this.socket = socket;
        }

        public void run()
        {
            try
            {
                System.out.println("Editor connected for testing...");

                // Communication Channel
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Echo Loop
                String line;
                while ((line = in.readLine()) != null)
                {
                    System.out.println("Received: " + line);
                    send(line);
                }

                // Clean Up
                out.close();
                in.close();
                socket.close();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void send(String msg)
        {
            System.out.println("Send: " + msg);
            out.println(msg);
        }
    }

    ///////////////////////////////////////////////////////////////////////

    public void getConnections() throws IOException
    {
        while (true)
        {
            EchoServerCommunicator communicator = new EchoServerCommunicator(listen.accept());
            communicator.setDaemon(true);
            communicator.start();
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting Up The EchoServer...");
        new EchoServer(new ServerSocket(4242)).getConnections();
    }
}
