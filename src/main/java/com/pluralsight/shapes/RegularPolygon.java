/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;
import org.jetbrains.annotations.*;

import java.awt.geom.*;

/**
 * Represents a regular polygon.
 * This shape's origin is in its center.
 */
public final class RegularPolygon extends Shape<RegularPolygon> {
    /**
     * The number of sides in this polygon.
     */
    @var
    @set(PropOption.Private)
    public final int numSides = 5;
    /**
     * The starting angle of one of the polygon's points.
     */
    @var
    @set(PropOption.Private)
    public final double angle = 0;
    /**
     * The distance from the origin to one of the polygon's points.
     */
    @var
    @set(PropOption.Private)
    public final double radius = 10;

    /**
     * Creates a new Regular Polygon with default values.
     */
    public RegularPolygon() {
    }

    private RegularPolygon(RegularPolygon copied) {
        super(copied);
        numSides = copied.numSides;
        angle = copied.angle;
        radius = copied.radius;
    }

    /**
     * Makes a new regular polygon with a different radius.
     *
     * @param radius The radius to use
     * @return A copied polygon with a new radius
     */
    @Contract("_ -> new")
    public RegularPolygon withRadius(double radius) {
        var cp = copy();
        cp.radius = radius;
        return cp;
    }

    /**
     * Makes a new regular polygon with a different angle.
     *
     * @param angle The angle to use
     * @return A copied polygon with a new angle
     */
    @Contract("_ -> new")
    public RegularPolygon withAngle(double angle) {
        var cp = copy();
        cp.angle = angle;
        return cp;
    }

    /**
     * Makes a new regular polygon with a different number of sides.
     *
     * @param numSides The number of sides to use
     * @return A copied polygon with a new number of sides
     */
    @Contract("_ -> new")
    public RegularPolygon withNumSides(int numSides) {
        var cp = copy();
        cp.numSides = numSides;
        return cp;
    }

    private double getTurnAngle() {
        return 360.0f / numSides;
    }

    private double getSideLength() {
        // (cos(0)*radius, sin(0)*radius) -> (cos(turnAngle)*radius, sin(turnAngle)*radius)
        return new Point2D.Double(radius, 0)
            .distance(
                new Point2D.Double(
                    Math.cos(Math.toRadians(turnAngle)) * radius,
                    Math.sin(Math.toRadians(turnAngle)) * radius
                )
            );
    }

    @Override
    protected void drawShape(Turtle turtle) {
        turtle.turnLeft(angle);
        turtle.penUp();
        turtle.forward(radius);
        turtle.turnLeft(90 + turnAngle / 2);
        turtle.penDown();
        for (int i = 0; i < numSides; i++) {
            turtle.forward(sideLength);
            turtle.turnLeft(turnAngle);
        }
    }

    @Override
    protected @NotNull RegularPolygon copy() {
        return new RegularPolygon(this);
    }
}
