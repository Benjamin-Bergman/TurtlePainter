/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

/**
 * Represents a circle with a specific radius.
 * This shape's origin is in its center.
 */
public final class Circle extends Shape<Circle> {
    /**
     * The radius of this circle.
     */
    @var
    public double radius = 10;

    /**
     * Creates a new Circle with default values.
     */
    public Circle() {
    }

    private Circle(Color color, double strokeWidth, Point2D origin, double radius) {
        super(color, strokeWidth, origin);
        this.radius = radius;
    }

    @SuppressWarnings("ReassignedVariable")
    // See https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
    private static List<Point2D> circlePoints(double r) {
        var pts = new ArrayList<Point2D>((int) r);
        var t1 = r / 16;
        var x = (int) r;
        var y = 0;
        while (x >= y) {
            pts.add(new Point2D.Double(x, y));
            y++;
            t1 += y;
            var t2 = t1 - x;
            if (t2 >= 0) {
                t1 = t2;
                x--;
            }
        }

        pts.ensureCapacity(pts.size() << 3);
        for (int i = pts.size() - 1; i >= 0; i--)
            pts.add(new Point2D.Double(pts[i].y, pts[i].x));
        for (int i = pts.size() - 1; i >= 0; i--)
            pts.add(new Point2D.Double(-pts[i].x, pts[i].y));
        for (int i = pts.size() - 1; i >= 0; i--)
            pts.add(new Point2D.Double(pts[i].x, -pts[i].y));

        return pts;
    }

    /**
     * Makes a new circle with a different radius.
     *
     * @param radius The radius to use
     * @return A copied circle with a new radius
     */
    public Circle withRadius(double radius) {
        var cp = copy();
        cp.radius = radius;
        return cp;
    }

    @Override
    protected void drawShape(Turtle turtle) {
        var pts = circlePoints(radius);
        var delay = turtle.delay;
        turtle.delay = 0;
        turtle.penUp();
        turtle.goTo(pts[0]);
        turtle.penDown();

        //noinspection ResultOfMethodCallIgnored
        pts.stream()
            .reduce((prev, pos) -> {
                turtle.heading = Math.toDegrees(Math.atan2(pos.y - prev.y, pos.x - prev.x));
                turtle.forward(pos.distance(prev));
                return pos;
            });

        turtle.delay = delay;
    }

    @Override
    protected Circle copy() {
        return new Circle(color, strokeWidth, origin, radius);
    }
}
