/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.drawing;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Source: https://codehs.com/sandbox/apcsa/java-turtle
 * <p>
 * A world for turtles to play inside of.
 * Usage example:
 * <pre>
 * World basic = new World();
 * World fancy = new World(640, 480, Color.YELLOW);
 *
 * Turtle t1 = new Turtle(basic);
 * Turtle t2 = new Turtle(fancy);
 *
 * t1.forward(100);
 * basic.erase();
 * t1.turnLeft(90);
 * t1.forward(100);
 *
 * t2.forward(100);
 * t2.turnLeft(90);
 * t2.forward(100);
 *
 * basic.saveAs("basicWorld.png");
 * fancy.saveAs("fancyWorld.png");
 * </pre>
 *
 * @author Luther Tychonievich. Released to the public domain.
 */
public class World extends JFrame {
    /// version number based on date of creation
    private static final long serialVersionUID = 20130902L;
    private final ArrayList<Turtle> turtles;
    private final Color backgroundColor;
    public int centerX;
    public int centerY;
    private BufferedImage overlay;
    private BufferedImage ground;
    private BufferedImage back;
    private BufferedImage front;
    private Graphics2D og;
    private Graphics2D gg;
    private Graphics2D bg;
    private Graphics2D fg;


    /**
     * Creates a new World for Turtles to play in.
     */
    public World() {
        this(600, 600);
    }

    public World(int width, int height) {
        this(width, height, Color.WHITE);
    }

    public World(int width, int height, Color backgroundColor) {
        super("Turtle World");

        this.backgroundColor = backgroundColor;
        resizeWorld(width, height);

        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent arg0) {
                dispose();
            }

            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }
        });

        turtles = new ArrayList<Turtle>();
    }

    public final void resizeWorld(int width, int height) {
        centerX = width / 2;
        centerY = height / 2;

        overlay = new BufferedImage(2 * centerX, 2 * centerY, BufferedImage.TYPE_INT_ARGB);
        ground = new BufferedImage(2 * centerX, 2 * centerY, BufferedImage.TYPE_INT_ARGB);
        back = new BufferedImage(2 * centerX, 2 * centerY, BufferedImage.TYPE_INT_ARGB);
        front = new BufferedImage(2 * centerX, 2 * centerY, BufferedImage.TYPE_INT_ARGB);

        og = (Graphics2D) overlay.getGraphics();
        gg = (Graphics2D) ground.getGraphics();
        bg = (Graphics2D) back.getGraphics();
        fg = (Graphics2D) front.getGraphics();
        og.setBackground(new Color(0, 0, 0, 0));
        gg.setBackground(backgroundColor);

        Graphics2D[] gs = {og, gg};
        for (Graphics2D g : gs) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }

        setContentPane(new JLabel(new ImageIcon(front)));

        clearOverlay();
        erase();

        repaint();
        setVisible(true);
    }

    /**
     * Erases all existing paths
     */
    public void erase() {
        gg.clearRect(0, 0, ground.getWidth(), ground.getHeight());
    }

    /**
     * Saves the current image to the specified file
     *
     * @param filename The name of the file to write
     * @throws IllegalArgumentException if any parameter is null or if the filename is not an image filename
     */
    public void saveAs(String filename) {
        try {
            int dot = filename.lastIndexOf('.');
            if (dot < 0 || dot == filename.length() - 1)
                throw new IllegalArgumentException("The filename must end in a valid image extension, like .png or .jpg");
            String ext = filename.substring(dot + 1).toLowerCase();
            File f = new File(filename);
            ImageIO.write(front, ext, f);
        } catch (Throwable t) {
            System.err.println("Error saving file: " + t.getMessage());
        }
    }

    /**
     * Erases all existing paths
     */
    private void clearOverlay() {
        og.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
    }

    /**
     * Should only called by the Turtle class constructor
     */
    void addTurtle(Turtle t) {
        turtles.add(t);
        turtleMoved();
    }

    /**
     * Should only called by Turtle class methods
     */
    void drawLine(Point2D p1, Point2D p2, double width, Color color) {
        // draw the line
        gg.setColor(color);
        gg.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Line2D.Double line = new Line2D.Double(p1, p2);
        gg.draw(line);

        // show the drawn lines
        blit();
    }

    private void blit() {
        bg.drawImage(ground, 0, 0, null);
        bg.drawImage(overlay, 0, 0, null);
        fg.drawImage(back, 0, 0, this);
        repaint();
    }

    /**
     * Should only called by Turtle class methods
     */
    void drawLine(Point2D p1, double nx, double ny, double width, Color color) {
        drawLine(p1, new Point2D.Double(nx, ny), width, color);
    }

    /**
     * Should only called by Turtle class methods
     */
    void turtleMoved() {
        // show the drawn lines
        clearOverlay();
        // add the turtles over top
        for (Turtle t : turtles) t._how_world_draw_turtles(og);
        // force the OS to show what's shown
        blit();
    }

    /**
     * To be used by Turtle class only
     *
     * @param img       the Image to draw
     * @param placement the Affine Transform to use in drawing it
     */
    void drawImage(Image img, AffineTransform placement) {
        gg.drawImage(img, placement, this);
        blit();
    }

}
