package shape.abstraction;

/*
 * Shape Abstraction
 * Created by Connor Crawford
 * 9/23/2015
 * Dr. Lakaemper
 * Section 5
 */

import simplegui.SimpleGUI;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.ListIterator;

public class Main implements simplegui.GUIListener {

    private SimpleGUI simpleGUI;
    private LinkedList<Point> origLinkedList;
    private ArrayList<LinkedList<Point>> linkedLists = new ArrayList<>();
    private int numPointsRead;

    public Main() {
        String filename = "shapelist.txt";
        try {
            this.origLinkedList = readFile(filename); // Get linked list from file, save the original version of it
            linkedLists.add(origLinkedList); // Add to the array list of linked lists
            this.simpleGUI = new SimpleGUI(800, 750);
            this.simpleGUI.centerGUIonScreen();
            this.simpleGUI.registerToGUI(this);
            this.simpleGUI.labelButton1("Abstract");
            this.simpleGUI.labelButton2("Original");
            reactToSlider(this.simpleGUI.getSliderValue()); // Draw at current slider value
        } catch (IOException e) {
            System.out.println("Error: Unable to read file");
            System.exit(-1);
        }
    }


    public static void main(String[] args) {
        Main m = new Main();
    }

    /*
     *  Parameters: String - The name of the file to be read
     *  Returns:    LinkedList<Point> - A linked list containing Point objects of everything able to be read
     *  Throws:     IOException
     *  This function will create a Point from every two ints it reads, and return a LinkedList of those Points
     */
    LinkedList<Point> readFile(String filename) throws IOException {
        LinkedList<Point> linkedList = new LinkedList<>();
        try(FileReader fileReader = new FileReader(filename)) {
            try (Scanner input = new Scanner(fileReader)) {
                while (input.hasNext()) {
                    if (input.hasNext()) {
                        int x = input.nextInt();
                        x += 175; // Shift to the right
                        int y = input.nextInt();
                        y = -y + 730; // flip over x-axis
                        linkedList.add(new Point(x, y));
                        this.numPointsRead++;
                    }
                }
            }
        }
        return linkedList;
    }

    /*
     * Parameters:  SimpleGUI - The SimpleGUI object on which the shape will be drawn.
     *              LinkedList<Point> - The LinkedList containing the points to be drawn. Cannot be empty.
     * This function will draw a shape from a LinkedList of Points
     */
    void draw(SimpleGUI simpleGUI, LinkedList<Point> linkedList) {
        if (linkedList.isEmpty())
            System.out.println("Error: No points entered.");
        else {
            ListIterator<Point> listIterator = linkedList.listIterator();
            Point current = listIterator.next(); // Set current to first element in linked list
            while (listIterator.hasNext()) {
                Point next = listIterator.next(); // Get next element in linked list
                simpleGUI.drawLine(current.getX(), current.getY(), next.getX(), next.getY()); // Connect current and next
                current = next; // replace current with next
            }
            simpleGUI.drawLine(linkedList.getFirst().getX(), linkedList.getFirst().getY(), current.getX(), current.getY()); // Connect first and last points
        }
    }

    /*
     * Parameters: LinkedList<Point> - The LinkedList containing the Points to have their significance calculated. Must have more than two points.
     * This function calculates the visual significance of each point in a Linked List using Discrete Curve Evolution
     */
    void calculateSignificance(LinkedList<Point> linkedList) {
        if (linkedList.isEmpty())
            System.out.println("Error: No points entered.");
        else if (linkedList.size() > 2){ // at least three elements are needed to calculate significance
            ListIterator<Point> listIterator = linkedList.listIterator();
            Point previous = listIterator.next(); // Set previous to first element in linked list
            previous.setSignificance(Double.MAX_VALUE); // Set first point's significance to the max value for a double so it's never deleted
            Point current = listIterator.next(); // Set current to second element in linked list
            Point next  = listIterator.next(); // Set next to third element in linked list
            do {
                double S1 = Math.sqrt(Math.pow(previous.getX() - current.getX(), 2) + Math.pow(previous.getY() - current.getY(), 2));
                double S2 = Math.sqrt(Math.pow(current.getX() - next.getX(), 2) + Math.pow(current.getY() - next.getY(), 2));
                double S3 = Math.sqrt(Math.pow(previous.getX() - next.getX(), 2) + Math.pow(previous.getY() - next.getY(), 2));
                current.setSignificance(S1 + S2 - S3);
                previous = current; // Set previous to current
                current = next; // Set current to next
                if (listIterator.hasNext())
                    next = listIterator.next(); // Get next point if there is one
            } while (listIterator.hasNext());
            next.setSignificance(Double.MAX_VALUE); // Set last point's significance to the max value for a double so it's never deleted
        } else
            System.out.println("Error: Not enough points to calculate significance");
    }

    /*
     * Parameters:  LinkedList<Point> - The LinkedList from which a Point will be removed. Must have more than two Points.
     * This function removes a Point from a LinkedList based on which Point has the lowest visual significance
     */
    void removeCoordinates(LinkedList<Point> linkedList) {
        if (linkedList.size() > 2) {
            ListIterator<Point> listIterator = linkedList.listIterator();
            double minSignificance = Double.MAX_VALUE; // Start with max value for double so everything is lower than it
            int minSigIndex = 0;
            while (listIterator.hasNext()) {
                Point current = listIterator.next();
                if (current.getSignificance() < minSignificance) { // Check if current's significance is less than the lowest
                    minSignificance = current.getSignificance(); // Set minSignificance to the current's significance
                    minSigIndex = listIterator.previousIndex(); // Set minSigIndex to index of current point
                }
            }
            linkedList.remove(minSigIndex); // Remove the point at the index that had the lowest significance
        } else
            System.out.println("Error: There must be more than two points to remove a point");
    }
    /*
     * Parameters:  int - The number of points to be searched for
     *              ArrayList<LinkedList<Point>> - An ArrayList of LinkedLists which will be searched
     * Returns:     LinkedList<Point> - A LinkedList with a size of numPoints
     * This function will check the ArrayList of LinkedLists to find a LinkedList of size numPoints.
     * If one is found, it is returned. If not, null is returned.
     */
    LinkedList<Point> getCreatedLinkedList(int numPoints, ArrayList<LinkedList<Point>> linkedLists) {
        for (LinkedList<Point> linkedList: linkedLists) {
            if (numPoints == linkedList.size())
                return linkedList;
        }
        return null;
    }

    /*
     * Parameters:  SimpleGUI - The SimpleGUI object on which the shape will be drawn.
     *              LinkedList<Point> - The LinkedList containing the points to be drawn. Cannot be empty.
     *              int - The number of points to be drawn
     * This function will remove Points from a copy of the LinkedList until it contains numPoints elements.
     * If this list hasn't been created before, it will create a new one and draw it.
     * If this list had been created before, it will get the pre-made LinkedList and draw it.
     */
    void drawAbstracted(SimpleGUI simpleGUI, LinkedList<Point> linkedList, int numPoints) {
        if (linkedList.isEmpty()) {
            System.out.println("Error: No points entered.");
        } else if (numPoints > linkedList.size()) {
            System.out.println("Error: Attempt to draw more points than available.");
        } else {
            LinkedList<Point> linkedListCopy;
            if ((linkedListCopy = getCreatedLinkedList(numPoints, this.linkedLists)) == null) {
                linkedListCopy = (LinkedList<Point>) linkedList.clone(); // Get a copy of the linked list so we don't alter the original
                while (linkedListCopy.size() != numPoints) { // Keep removing points until we're at the desired amount
                    calculateSignificance(linkedListCopy); // Significance must be recalculated every time a point is removed
                    removeCoordinates(linkedListCopy);
                }
                linkedLists.add(linkedListCopy); // List isn't already created, so add to the array list of linked lists
            }
            simpleGUI.eraseAllDrawables();
            draw(simpleGUI, linkedListCopy);
            simpleGUI.drawText(numPoints + " Points", this.simpleGUI.getWidth() / 2 - 30, this.simpleGUI.getHeight()); // Draw text in bottom-center saying how many points are in drawing
        }
    }

    @Override
    public void reactToButton1() {
        reactToSlider(this.simpleGUI.getSliderValue());
    }

    @Override
    public void reactToButton2() {
        drawAbstracted(this.simpleGUI, this.origLinkedList, numPointsRead);
    }

    @Override
    public void reactToSwitch(boolean b) {
        // Doesn't do anything
    }

    @Override
    public void reactToSlider(int i) {
        int numPoints = (int) (i * ((numPointsRead - 20) / 100.0) + 20); // Slider only has range of 1-100, so adjust the range to fit from 20 to numPointsRead
        drawAbstracted(this.simpleGUI, this.origLinkedList, numPoints); // Draw shape with numPoints number of points
    }
}
