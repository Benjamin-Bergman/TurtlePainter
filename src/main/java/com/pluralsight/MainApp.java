/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.Shape;
import com.pluralsight.shapes.*;

import java.awt.*;
import java.util.*;

@SuppressWarnings("UtilityClass")
final class MainApp {
    public static void main(String[] args) {
        var world = new World();
        var turtle = new Turtle(world);

        turtle.delay = 0.3;

        ArrayList<Shape<?>> shapes = new ArrayList<>();
        shapes.add(new Circle());
        shapes.add(new Circle().withColor(Color.BLUE).withRadius(20));
        shapes.add(new Circle().withRadius(30).withColor(Color.RED));
        shapes.add(new Rect().withWidth(40).withHeight(40));
        shapes.add(new RegularPolygon().withRadius(40));
        Painting p = new Painting(100, 100, shapes, world, turtle);

        try (Scanner scanner = new Scanner(System.in)) {
            do p.drawAsync();
            while (!"exit".equals(scanner.nextLine()));
        }
    }
}
