import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Client-Server Graphical Editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012, Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015
 * @author CBK, Dartmouth CS 10, Spring 2016, Fall 2016
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */

public class Editor extends JFrame
{
    // IP address of sketch server, "localhost" for your own machine or ask a friend for their IP address.
    private static String serverIP = "localhost";

    private static final int width = 800, height = 800; // Canvas Size

    // Current Settings -> GUI
    public enum Mode
    {
        DRAW, MOVE, RECOLOR, DELETE
    }

    private Mode mode = Mode.DRAW; // Drawing/moving/recoloring/deleting objects.
    private String shapeType = "Ellipse"; // Type of object to add.
    private Color color = Color.black; // Current drawing color.

    // Drawing State
    private Shape currentShape = null; // Current shape (if any) being drawn.
    private Sketch sketch; // Holds and handles all the completed objects.
    private int currentShapeID = -1; // Current shape ID (if any; else -1) being moved.
    private Point drawFrom = null; // Where the drawing started.
    private Point moveFrom = null; // Where the object is as it's being dragged.

    // Communication
    private EditorCommunicator communicator; // Communication with the sketch server.

    public Editor()
    {
        super("Graphical Editor");

        sketch = new Sketch();

        // Connect to server.
        communicator = new EditorCommunicator(serverIP, this);
        communicator.start();

        // Helpers to create the canvas and GUI (buttons, etc.).
        JComponent canvas = setupCanvas();
        JComponent gui = setupGUI();

        // Put the buttons and canvas together into the window.
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(gui, BorderLayout.NORTH);

        // Usual Initialization
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Setup Canvas - Creates a component to draw into.
     */
    private JComponent setupCanvas()
    {
        JComponent canvas = new JComponent()
        {
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                drawSketch(g); // Calls a helper method to draw the sketch on g.
                // System.out.println("Repainting!");
            }
        };

        canvas.setPreferredSize(new Dimension(width, height));

        canvas.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent event)
            {
                handlePress(event.getPoint()); // Calls a helper method to handle the mouse press.
                // System.out.println("Pressed At " + event.getPoint());
            }

            public void mouseReleased(MouseEvent event)
            {
                handleRelease(); // Calls a helper method to handle the mouse release.
                // System.out.println("Released At " + event.getPoint());
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter()
        {
            public void mouseDragged(MouseEvent event)
            {
                handleDrag(event.getPoint()); // Calls a helper method to handle the mouse drag.
                // System.out.println("Dragged To " + event.getPoint());
            }
        });

        return canvas;
    }

    /**
     * Setup GUI - Creates a panel with all the buttons
     */
    private JComponent setupGUI()
    {
        // Select type of shape.
        String[] shapes = {"Ellipse", "Polyline", "Rectangle", "Segment"};
        JComboBox<String> shapeB = new JComboBox<>(shapes);
        shapeB.addActionListener(e -> shapeType = (String) ((JComboBox<String>) e.getSource()).getSelectedItem());

        // Select drawing/recoloring color. Following Oracle example.
        JButton chooseColorB = new JButton("Choose Color");
        JColorChooser colorChooser = new JColorChooser();
        JLabel colorL = new JLabel();
        colorL.setBackground(Color.black);
        colorL.setOpaque(true);
        colorL.setBorder(BorderFactory.createLineBorder(Color.black));
        colorL.setPreferredSize(new Dimension(25, 25));
        JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
                "Pick A Color",
                true,  // Modal
                colorChooser,
                e ->
                {
                    color = colorChooser.getColor();
                    colorL.setBackground(color);
                },  // OK Button
                null); // No CANCEL button handler.
        chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

        // Mode: draw, move, recolor, or delete.
        JRadioButton drawB = new JRadioButton("Draw");
        drawB.addActionListener(e -> mode = Mode.DRAW);
        drawB.setSelected(true);
        JRadioButton moveB = new JRadioButton("Move");
        moveB.addActionListener(e -> mode = Mode.MOVE);
        JRadioButton recolorB = new JRadioButton("Recolor");
        recolorB.addActionListener(e -> mode = Mode.RECOLOR);
        JRadioButton deleteB = new JRadioButton("Delete");
        deleteB.addActionListener(e -> mode = Mode.DELETE);
        ButtonGroup modes = new ButtonGroup(); // Make them act as radios (only one selected).
        modes.add(drawB);
        modes.add(moveB);
        modes.add(recolorB);
        modes.add(deleteB);
        JPanel modesP = new JPanel(new GridLayout(1, 0)); // Group them on the GUI.
        modesP.add(drawB);
        modesP.add(moveB);
        modesP.add(recolorB);
        modesP.add(deleteB);

        // Put all the stuff into a panel.
        JComponent gui = new JPanel();
        gui.setLayout(new FlowLayout());
        gui.add(shapeB);
        gui.add(chooseColorB);
        gui.add(colorL);
        gui.add(modesP);
        return gui;
    }

    /**
     * Get Sketch - Getter for the sketch instance variable.
     */
    public Sketch getSketch()
    {
        return sketch;
    }

    /**
     * Draw Sketch - Draws all the shapes in the sketch, along with the object currently being drawn in this editor (not
     * yet part of the sketch).
     */
    public synchronized void drawSketch(Graphics g)
    {
        if (!sketch.IDMap.isEmpty())
        {
            // Drawing all the shapes in the sketch.
            for (int i = 0; i <= sketch.IDMap.lastKey(); i += 1)
            {
                if (sketch.IDMap.containsKey(i))
                    sketch.IDMap.get(i).draw(g);
            }
        }

        // Drawing the shape currently being drawn in the editor (not yet part of the sketch).
        // If the current shape exists...
        if (currentShape != null)
            currentShape.draw(g); // Draw it.

        repaint();
    }

    /**
     * Helper Method - In drawing mode, start a new object, in moving mode, (request to) start dragging if clicked in a
     * shape, in recoloring mode, (request to) change clicked shape's color, in deleting mode, (request to) delete
     * clicked shape.
     */
    private void handlePress(Point p)
    {
        // In drawing mode, start a new object.
        if (mode == Mode.DRAW)
        {
            // Updating the drawFrom and moveFrom points.
            drawFrom = p;
            moveFrom = p;

            // Creating a new shape based on the shapeType with the appropriate location and color.
            switch (shapeType)
            {
                case "Ellipse" -> currentShape = new Ellipse(drawFrom.x, drawFrom.y, color);
                case "Rectangle" -> currentShape = new Rectangle(drawFrom.x, drawFrom.y, color);
                case "Segment" -> currentShape = new Segment(drawFrom.x, drawFrom.y, color);
                case "Polyline" -> currentShape = new Polyline(drawFrom.x, drawFrom.y, color);
            }
        }

        // In moving mode, (request to) start dragging if clicked in a shape.
        else if (mode == Editor.Mode.MOVE)
        {
            // Checking to make sure an object was clicked.
            if (sketch.IDFromClicked(p) != -1)
            {
                currentShapeID = sketch.IDFromClicked(p);
                currentShape = sketch.IDMap.get(sketch.IDFromClicked(p));
                moveFrom = p;
            }
        }

        // In recoloring mode, (request to) change clicked shape's color.
        else if (mode == Mode.RECOLOR)
        {
            // Checking to make sure an object was clicked.
            if (sketch.IDFromClicked(p) != -1)
                communicator.send("RECOLOR " + sketch.IDFromClicked(p) + " " + color.getRGB());
        }

        // In deleting mode, (request to) delete clicked shape.
        else if (mode == Mode.DELETE)
        {
            // Checking to make sure an object was clicked.
            if (sketch.IDFromClicked(p) != -1)
                communicator.send("DELETE " + sketch.IDFromClicked(p));
        }

        repaint();
    }

    /**
     * Helper Method - In drawing mode, update the other corner of the object, in moving mode, (request to) drag the object.
     */
    private void handleDrag(Point p)
    {
        // In drawing mode, revise the shape as it is stretched out.
        if (mode == Mode.DRAW)
        {
            // Check to make sure there is a shape.
            if (currentShape != null)
            {
                if (shapeType.equals("Ellipse"))
                {
                    ((Ellipse) currentShape).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);

                    // Updating the moveFrom location based on the location of the center of the shape.
                    moveFrom = new Point((int) (drawFrom.x + ((p.x - drawFrom.x) / 2.0)), (int) (drawFrom.y + ((p.y - drawFrom.y) / 2.0)));
                }

                else if (shapeType.equals("Rectangle"))
                {
                    ((Rectangle) currentShape).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);

                    // Updating the moveFrom location based on the location of the center of the shape.
                    moveFrom = new Point((int) (drawFrom.x + ((p.x - drawFrom.x) / 2.0)), (int) (drawFrom.y + ((p.y - drawFrom.y) / 2.0)));
                }

                else if (shapeType.equals("Segment"))
                {
                    ((Segment) currentShape).setEnd(p.x, p.y);

                    // Updating the moveFrom location based on the location of the center of the shape.
                    moveFrom = new Point((int) ((p.x - drawFrom.x) / 2.0), (int) ((p.y - drawFrom.y) / 2.0));
                }

                else if (shapeType.equals("Polyline"))
                {
                    ((Polyline) currentShape).addPoint(p.x, p.y);

                    // Updating the moveFrom location based on the location of the center of the shape.
                    moveFrom = new Point((int) ((p.x - drawFrom.x) / 2.0), (int) ((p.y - drawFrom.y) / 2.0));
                }
            }
        }

        // In moving mode, (request to) drag the object, and keep track of where next step is from.
        else if (mode == Editor.Mode.MOVE)
        {
            if (currentShape != null && moveFrom != null)
            {
                communicator.send("MOVE " + currentShapeID + " " + (p.x - moveFrom.x) + " " + (p.y - moveFrom.y));

                // Updating the moveFrom location based on the new point.
                moveFrom = p;
            }
        }

        // Refreshing the canvas when the appearance has changed.
        repaint();
    }

    /**
     * Helper Method - In drawing mode, pass the add new object request on to the server, in moving mode, release it.
     */
    private void handleRelease()
    {
        // Check to see if the mode is DRAW.
        if (mode == Mode.DRAW)
        {
            // Passing the add new object request on to the server and updating the current shape.
            communicator.send("ADD " + currentShape);
        }

        currentShape = null;

        // Refreshing the canvas when the appearance has changed. In moving mode, stop dragging the object.
        repaint();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new Editor();
            }
        });
    }
}
