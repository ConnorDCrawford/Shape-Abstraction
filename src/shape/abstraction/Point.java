package shape.abstraction;

/*
 * Shape Abstraction
 * Created by Connor Crawford
 * 9/23/2015
 * Dr. Lakaemper
 * Section 5
 */

public class Point {
    private int x, y;
    private double significance = 0;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getSignificance() {
        return significance;
    }

    public void setSignificance(double significance) {
        this.significance = significance;
    }

    public String toString(){
        return "<"+x+", "+y+">";
    }

}
