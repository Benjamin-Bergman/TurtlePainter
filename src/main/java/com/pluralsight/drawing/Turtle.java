/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.drawing;

import javax.imageio.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Source: https://codehs.com/sandbox/apcsa/java-turtle
 * <p>
 * Turtles inspired by Logo (by Bobrow, Feurzeig, Papert, and Soloon, 1967),
 * which in turn was inspired by the turtle robots built by Walter in the late 1940s.
 * <p>
 * Usage example:
 * <pre>
 * com.pluralsight.drawing.World w = new com.pluralsight.drawing.World();
 *
 * com.pluralsight.drawing.Turtle t = new com.pluralsight.drawing.Turtle(w);
 * t.forward(300);
 * t.setPenWidth(10);
 * t.backward(100);
 * t.right(135);
 * t.penUp();
 * t.forward(50);
 * t.drop("http://www.extremenxt.com/elsie.jpg");
 * </pre>
 *
 * @author Luther Tychonievich. Released to the public domain.
 */
public class Turtle {
    /// version number based on date of creation
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 20140120L;

    private static final Map<String, BufferedImage> cachedPictures = new TreeMap<String, BufferedImage>();
    private static final Color[] base = {
        Color.BLACK,
        Color.RED,
        Color.BLUE,
        Color.MAGENTA,
        Color.CYAN,
    };
    private static int baseIndex;
    private final World world;
    private final Point2D.Double location;
    private double theta;
    private boolean isdown;
    // private GeneralPath trail; // not used
    private Color color;
    private double shellSize;
    private int pause = 200;
    private double penWidth;

    /**
     * Makes a new turtle in the center of the world.
     *
     * @param w the world
     */
    public Turtle(World w) {
        this(w, 0, 0);
    }

    /**
     * Makes a new turtle at the specified point within the world.
     *
     * @param x the x coordinate, in pixels; 0 is the center; bigger numbers to left
     * @param y the y coordinate, in pixels; 0 is the center; bigger numbers down
     * @param w the world
     */
    public Turtle(double x, double y, World w) {
        this(w, x, y);
    }

    /**
     * Makes a new turtle at the specified point within the world.
     *
     * @param w the world
     * @param x the x coordinate, in pixels; 0 is the center; bigger numbers to left
     * @param y the y coordinate, in pixels; 0 is the center; bigger numbers down
     */
    public Turtle(World w, double x, double y) {
        location = new Point2D.Double(x + w.centerX, y + w.centerY);
        // this.trail = new GeneralPath(); // not used
        // this.trail.moveTo(this.location.x, this.location.y); // not used
        theta = 0;
        world = w;
        color = base[baseIndex];
        baseIndex = (baseIndex + 1) % base.length;
        penWidth = 1;
        isdown = true;
        shellSize = 8;

        w.addTurtle(this);
    }

    /**
     * Moves the turtle 100 pixels in the direction it is facing.
     */
    public void forward() {
        forward(100);
    }

    /**
     * Moves the turtle the specified distance in the direction it is facing.
     *
     * @param d the number of pixels to move
     */
    public void forward(double d) {
        cornerGoTo(location.x + Math.cos(theta) * d, location.y + Math.sin(theta) * d);
    }

    /**
     * Moves the turtle 100 pixels in the opposite of the direction it is facing.
     */
    public void backward() {
        backward(100);
    }

    /**
     * Moves the turtle the specified distance in the opposite direction from the one it is facing.
     *
     * @param d the number of pixels to move
     */
    public void backward(double d) {
        forward(-d);
    }

    /**
     * Turns the turtle clockwise in place.
     *
     * @param degrees the number of degrees to turn
     */
    public void turnRight(double degrees) {
        theta += Math.PI * degrees / 180;
        while (theta > Math.PI) theta -= Math.PI * 2;
        while (theta <= -Math.PI) theta += Math.PI * 2;
        world.turtleMoved();
        pause();
    }

    /**
     * Turns the turtle counterclockwise in place.
     *
     * @param degrees the number of degrees to turn
     */
    public void turnLeft(double degrees) {
        turnRight(-degrees);
    }

    /**
     * Stops the turtle from leaving a trail.
     */
    public void penUp() {
        isdown = false;
    }

    /**
     * Causes the turtle to leave a trail.
     */
    public void penDown() {
        isdown = true;
    }

    /**
     * Check the pen state
     *
     * @return true if the pen is down, false otherwise
     */
    public boolean isPenDown() {
        return isdown;
    }

    /**
     * Place a picture on the screen where the turtle currently is;
     * make it 100 pixels wide.
     *
     * @param filename the file name or URL of the image to be drawn
     * @return true if the image was found, false otherwise
     */
    public boolean drop(String filename) {
        return drop(filename, 100);
    }

    /**
     * Place a picture on the screen where the turtle currently is.
     *
     * @param filename the file name or URL of the image to be drawn
     * @param size     how big the image should be in pixels
     * @return true if the image was found, false otherwise
     */
    public boolean drop(String filename, double size) {
        try {
            BufferedImage pic;
            if (cachedPictures.containsKey(filename)) pic = cachedPictures.get(filename);
            else {
                try {
                    pic = ImageIO.read(new URL(filename).openStream());
                } catch (Throwable ex) {
                    pic = ImageIO.read(new File(filename));
                }
                cachedPictures.put(filename, pic);
            }
            double scale = size / Math.max(pic.getWidth(), pic.getHeight());

            AffineTransform af = new AffineTransform();
            af.translate(location.x, location.y);
            af.rotate(theta + Math.PI / 2);
            af.translate(-size / 2, -size / 2);
            af.scale(scale, scale);
            world.drawImage(pic, af);
            pause();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Returns the current Color of the com.pluralsight.drawing.Turtle.
     *
     * @return The current Color of the pen.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Changes the current Color of the com.pluralsight.drawing.Turtle.
     *
     * @param color The new Color to use in drawing
     */
    public void setColor(Color color) {
        this.color = color;
        world.turtleMoved();
        pause();
    }

    /**
     * Returns the current width of the turtle's pen.
     *
     * @return The new pen width, in pixels.
     */
    public double getPenWidth() {
        return penWidth;
    }

    /**
     * Sets the width of the pen.
     *
     * @param width The new pen width, in pixels.
     */
    public void setPenWidth(double width) {
        if (width <= 0) throw new IllegalArgumentException("Width must be positive");
        penWidth = width;
    }

    public double getShellSize() {
        return shellSize;
    }

    public void setShellSize(double shellSize) {
        this.shellSize = shellSize;
        world.turtleMoved();
        pause();
    }

    /**
     * Find out what direction the com.pluralsight.drawing.Turtle is facing
     *
     * @return angle in degrees; 0 is right, 90 is up, etc
     */
    public double getHeading() {
        return theta * 180 / Math.PI;
    }

    /**
     * Set the direction the com.pluralsight.drawing.Turtle is facing
     *
     * @param angle in degrees; 0 is right, 90 is up, etc
     */
    public void setHeading(double angle) {
        theta = angle * Math.PI / 180;
        world.turtleMoved();
        pause();
    }

    /**
     * Find out where the turtle is located
     *
     * @return The location of the turtle. (0,0) is the center of the screen, +x is rightward, +y is downward.
     */
    public Point2D getLocation() {
        // fix in following line suggested Anna Cuddeback 2018-11-28
        return new Point2D.Double(location.x - world.centerX, -location.y + world.centerY);
    }

    /**
     * Move the turtle to a particular location. It might leave a trail depending on if the pen is down or not.
     *
     * @param where The new location for the turtle. (0,0) is the center of the screen, +x is rightward, +y is downward.
     */
    public void goTo(Point2D where) {
        cornerGoTo(where.getX() + world.centerX, -where.getY() + world.centerY);
    }

    public void goTo(double x, double y) {
        cornerGoTo(x + world.centerX, -y + world.centerY);
    }

    /**
     * Seconds to pause between each turtle movement
     *
     * @return the seconds currently paused
     */
    public double getDelay() {
        return pause * 0.001;
    }

    /**
     * Seconds to pause between each turtle movement
     *
     * @param seconds The seconds to pause
     */
    public void setDelay(double seconds) {
        pause = (int) (seconds * 1000);
    }

    public void pause() {
        if (pause == 0)
            return;
        Thread.sleep(pause);
    }

    /**
     * Draws the shell of the turtle.
     * Should only be called by com.pluralsight.drawing.World class
     *
     * @param g the graphics object to draw with
     */
    void _how_world_draw_turtles(Graphics2D g) {

        // // Other way to draw trails; can't change color part-way through though
        // g.setColor(color);
        // g.setStroke(new BasicStroke((float)this.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // g.draw(this.trail);
        // // end other way to draw tails

        // The following draws a picture of a turtle

        // three shapes
        GeneralPath back = new GeneralPath(); // the bigger shell
        GeneralPath back2 = new GeneralPath(); // the paler inner shell
        GeneralPath body = new GeneralPath(); // the head, legs, and tail
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        double x = location.x;
        double y = location.y;
        double w = shellSize;
        Ellipse2D leftEye = new Ellipse2D.Double(x + 1.55 * w * c + 0.15 * w * s - 0.1 * w, y + 1.55 * w * s - 0.15 * w * c - 0.1 * w, 0.2 * w, 0.2 * w);
        Ellipse2D rightEye = new Ellipse2D.Double(x + 1.55 * w * c - 0.15 * w * s - 0.1 * w, y + 1.55 * w * s + 0.15 * w * c - 0.1 * w, 0.2 * w, 0.2 * w);

        body.moveTo(x + w * 0.9 * c + w * 0.4 * s, y + w * 0.9 * s - w * 0.4 * c);
        body.curveTo(
            x + w * 1.6 * c + w * 0.4 * s, y + w * 1.6 * s - w * 0.4 * c,
            x + w * 1.8 * c + w * 0.3 * s, y + w * 1.8 * s - w * 0.3 * c,
            x + w * 1.8 * c + w * 0.0 * s, y + w * 1.8 * s - w * 0.0 * c
        );
        body.curveTo(
            x + w * 1.8 * c - w * 0.3 * s, y + w * 1.8 * s + w * 0.3 * c,
            x + w * 1.6 * c - w * 0.4 * s, y + w * 1.6 * s + w * 0.4 * c,
            x + w * 0.9 * c - w * 0.4 * s, y + w * 0.9 * s + w * 0.4 * c
        );
        body.lineTo(x + w * 0.8 * c - w * 1.2 * s, y + w * 0.8 * s + w * 1.2 * c);
        body.lineTo(x + w * 0.5 * c - w * 1.2 * s, y + w * 0.5 * s + w * 1.2 * c);
        body.lineTo(x + w * 0.5 * c - w * 0.6 * s, y + w * 0.5 * s + w * 0.6 * c);
        body.lineTo(x - w * 0.5 * c - w * 0.6 * s, y - w * 0.5 * s + w * 0.6 * c);
        body.lineTo(x - w * 0.6 * c - w * 1.2 * s, y - w * 0.6 * s + w * 1.2 * c);
        body.lineTo(x - w * 0.9 * c - w * 1.15 * s, y - w * 0.9 * s + w * 1.15 * c);
        body.lineTo(x - w * 0.85 * c - w * 0.2 * s, y - w * 0.85 * s + w * 0.2 * c);
        body.lineTo(x - w * 1.6 * c - w * 0.0 * s, y - w * 1.6 * s + w * 0.0 * c);
        body.lineTo(x - w * 0.85 * c + w * 0.2 * s, y - w * 0.85 * s - w * 0.2 * c);
        body.lineTo(x - w * 0.9 * c + w * 1.15 * s, y - w * 0.9 * s - w * 1.15 * c);
        body.lineTo(x - w * 0.6 * c + w * 1.2 * s, y - w * 0.6 * s - w * 1.2 * c);
        body.lineTo(x - w * 0.5 * c + w * 0.6 * s, y - w * 0.5 * s - w * 0.6 * c);
        body.lineTo(x + w * 0.5 * c + w * 0.6 * s, y + w * 0.5 * s - w * 0.6 * c);
        body.lineTo(x + w * 0.5 * c + w * 1.2 * s, y + w * 0.5 * s - w * 1.2 * c);
        body.lineTo(x + w * 0.8 * c + w * 1.2 * s, y + w * 0.8 * s - w * 1.2 * c);
        body.closePath();

        back.moveTo(x + w * 1.2 * c, y + w * 1.2 * s);
        back.curveTo(
            x + w * 1.2 * c + w * 0.6 * s, y + w * 1.2 * s - w * 0.6 * c,
            x + w * 0.7 * c + w * s, y + w * 0.7 * s - w * c,
            x + w * s, y - w * c
        );
        back.curveTo(
            x - w * 0.7 * c + w * s, y - w * 0.7 * s - w * c,
            x - w * 1.2 * c + w * 0.6 * s, y - w * 1.2 * s - w * 0.6 * c,
            x - w * 1.2 * c, y - w * 1.2 * s
        );
        back.curveTo(
            x - w * 1.2 * c - w * 0.6 * s, y - w * 1.2 * s + w * 0.6 * c,
            x - w * 0.7 * c - w * s, y - w * 0.7 * s + w * c,
            x - w * s, y + w * c
        );
        back.curveTo(
            x + w * 0.7 * c - w * s, y + w * 0.7 * s + w * c,
            x + w * 1.2 * c - w * 0.6 * s, y + w * 1.2 * s + w * 0.6 * c,
            x + w * 1.2 * c, y + w * 1.2 * s
        );

        w *= 0.7;
        back2.moveTo(x + w * 1.2 * c, y + w * 1.2 * s);
        back2.curveTo(
            x + w * 1.2 * c + w * 0.6 * s, y + w * 1.2 * s - w * 0.6 * c,
            x + w * 0.7 * c + w * s, y + w * 0.7 * s - w * c,
            x + w * s, y - w * c
        );
        back2.curveTo(
            x - w * 0.7 * c + w * s, y - w * 0.7 * s - w * c,
            x - w * 1.2 * c + w * 0.6 * s, y - w * 1.2 * s - w * 0.6 * c,
            x - w * 1.2 * c, y - w * 1.2 * s
        );
        back2.curveTo(
            x - w * 1.2 * c - w * 0.6 * s, y - w * 1.2 * s + w * 0.6 * c,
            x - w * 0.7 * c - w * s, y - w * 0.7 * s + w * c,
            x - w * s, y + w * c
        );
        back2.curveTo(
            x + w * 0.7 * c - w * s, y + w * 0.7 * s + w * c,
            x + w * 1.2 * c - w * 0.6 * s, y + w * 1.2 * s + w * 0.6 * c,
            x + w * 1.2 * c, y + w * 1.2 * s
        );

        int gap = 48;
        Color midColor = new Color(
            Math.max(Math.min(color.getRed(), 255 - gap), gap),
            Math.max(Math.min(color.getGreen(), 255 - gap), gap),
            Math.max(Math.min(color.getBlue(), 255 - gap), gap)
        );
        Color lightColor = new Color(
            midColor.getRed() + gap,
            midColor.getGreen() + gap,
            midColor.getBlue() + gap
        );
        Color darkColor = new Color(
            midColor.getRed() - gap,
            midColor.getGreen() - gap,
            midColor.getBlue() - gap
        );


        g.setColor(darkColor);
        g.fill(body);
        g.setColor(midColor);
        g.fill(back);
        g.setColor(lightColor);
        g.fill(back2);
        g.setColor(Color.WHITE);
        g.fill(leftEye);
        g.fill(rightEye);

    }

    /**
     * Move the turtle to a particular location. It might leave a trail depending on if the pen is down or not.
     *
     * @param where The new location for the turtle. (0,0) is the top left of the screen, +x is rightward, +y is downward.
     */
    protected void cornerGoTo(Point2D where) {
        cornerGoTo(where.getX(), where.getY());
    }

    /**
     * Move the turtle to a particular location. It might leave a trail depending on if the pen is down or not.
     *
     * @param x The new x location for the turtle. 0 is the center of the screen, bigger numbers to the right
     * @param y The new y location for the turtle. 0 is the center of the screen, bigger numbers lower down
     */
    protected void cornerGoTo(double x, double y) {
        double ox = location.x;
        double oy = location.y;
        location.x = x;
        location.y = y;
        if (isdown) {
            world.drawLine(location, ox, oy, penWidth, color);
            world.turtleMoved();
            pause();
        } else {
            world.turtleMoved();
            pause();
        }

    }


}
