/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.*;

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

//        var rect = new Rect()
//            .withOrigin(new Point2D.Double(-256, 256))
//            .withWidth(512)
//            .withHeight(512);
//
//        var components = new float[]{
//            0.0f,
//            1.0f,
//            0.7f
//        };
//
//        UnaryOperator<Rect> updateColors = r -> {
//            components[0] += 0.1f;
//            return r.withColor(new Color(Color.HSBtoRGB(components[0], components[1], components[2])));
//        };
//
//        rect = updateColors.apply(rect);
//        rect.draw(turtle);
//
//        for (int scale = 256; scale > 0; scale >>= 1) {
//            rect = updateColors.apply(rect);
//            rect.withWidth(scale).draw(turtle);
//            rect = updateColors.apply(rect);
//            rect.withHeight(scale).draw(turtle);
//        }

//        new Circle().withRadius(100).draw(turtle);

//        new Circle().withRadius(1).draw(turtle);
//
//        var poly = new RegularPolygon().withNumSides(8).withRadius(100).withColor(Color.RED);
//        var poly2 = poly.withAngle(22.5).withColor(Color.BLUE);
//        for (int rad = 20; rad < width / 2; rad += 40) {
//            poly.withRadius(rad).draw(turtle);
//            poly2.withRadius(rad).draw(turtle);
//        }

        var shape = new RegularPolygon().withRadius(200).withAngle(90);
        for (int sides = 3; sides < 20; sides++)
            shape.withNumSides(sides).draw(turtle);

        turtle.penUp();
        turtle.goTo(-width - 100, -height - 100);
    }
}
