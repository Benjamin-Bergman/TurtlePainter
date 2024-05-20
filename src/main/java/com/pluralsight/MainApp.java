/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;

import java.awt.*;

@SuppressWarnings("UtilityClass")
final class MainApp {
    @SuppressWarnings("FeatureEnvy")
    public static void main(String[] args) {
        // This starter code to get you familiar with how
        // the TurtleLogo application works

        // The world is your canvas
        int width = 200;
        int height = 200;
        World world = new World(width, height);
        Turtle turtle = new Turtle(world, -100, -100);

        // calculate the hypotenuse (diagonal)
        // a2 + b2 = c2

        turtle.setPenWidth(3);
        turtle.setColor(Color.GREEN);

        turtle.turnRight(45);
        double widthSquared = Math.pow(width, 2);
        double heightSquared = Math.pow(height, 2);
        double hypotenuse = Math.sqrt(widthSquared + heightSquared);
        turtle.forward(hypotenuse);

        turtle.penUp();
        turtle.goTo(100, 100);
        turtle.turnRight(90);

        turtle.penDown();
        turtle.forward(hypotenuse);
    }
}
