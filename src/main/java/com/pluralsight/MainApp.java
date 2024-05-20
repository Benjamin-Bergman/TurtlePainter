/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.*;

import java.awt.*;
import java.awt.geom.*;

@SuppressWarnings("UtilityClass")
final class MainApp {
    public static void main(String[] args) {
        // This starter code to get you familiar with how
        // the TurtleLogo application works

        // The world is your canvas
        int width = 1024;
        int height = 768;
        World world = new World(width, height);
        Turtle turtle = new Turtle(world, -100, -100);
        turtle.delay = 0.05;

        var rect = new Rect()
            .withOrigin(new Point2D.Double(-256, 256))
            .withWidth(512)
            .withHeight(512);

        var components = new float[]{
            0.0f,
            1.0f,
            0.7f
        };

        Runnable updateColors = () -> {
            components[0] += 0.1f;
            rect.color = new Color(Color.HSBtoRGB(components[0], components[1], components[2]));
        };

        updateColors.run();
        rect.draw(turtle);

        for (int scale = 256; scale > 0; scale >>= 1) {
            updateColors.run();
            rect.withWidth(scale).draw(turtle);
            updateColors.run();
            rect.withHeight(scale).draw(turtle);
        }

        turtle.penUp();
        turtle.goTo(-width - 100, -height - 100);
    }
}
