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
        var world = new World(600, 600);
        var turtle = new Turtle(world);

        turtle.delay = 0.1;

        ArrayList<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle());
        shapes.add(new Circle().withColor(Color.BLUE).withRadius(280));
        shapes.add(new Circle().withRadius(30));
        shapes.add(new Rect().withWidth(40).withHeight(40));
        shapes.add(new RegularPolygon().withRadius(40));
        Painting p = new Painting(600, 600, shapes, world, turtle);

        Scanner s = new Scanner(System.in);
        do p.drawAsync();
        while (!"exit".equals(s.nextLine()));
    }
}
