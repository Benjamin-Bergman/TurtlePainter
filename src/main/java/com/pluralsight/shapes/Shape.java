/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

/**
 * Represents a shape which can be drawn with a {@link Turtle}.
 *
 * @param <T> The implementing class
 */
public abstract class Shape<T extends Shape<T>> implements TurtleDrawable, Serializable {
    private static int UUIDCounter;
    /**
     * The color used to draw this shape.
     */
    @var
    @set(PropOption.Private)
    public final Color color = Color.BLACK;
    /**
     * The width of stroke to use when drawing this shape.
     */
    @var
    @set(PropOption.Private)
    public final double strokeWidth = 1;
    /**
     * The origin point for this shape.
     */
    @var
    @set(PropOption.Private)
    public final Point2D origin = new Point2D.Double(0, 0);
    /**
     * A unique identifier for this shape.
     * <p>
     * This is guaranteed to be unique for any given
     * execution but may not be unique across multiple executions.
     */
    @val
    public transient int UUID;

    protected Shape() {
        UUID = UUIDCounter;
        UUIDCounter++;
    }

    /**
     * Copy constructor.
     *
     * @param copied The shape to copy from
     */
    protected Shape(Shape copied) {
        this();
        color = copied.color;
        strokeWidth = copied.strokeWidth;
        origin = copied.origin;
    }

    @Override
    public final void draw(Turtle turtle) {
        var delay = turtle.delay;
        turtle.delay = 0;
        turtle.penUp();
        turtle.setColor(color);
        turtle.setPenWidth(strokeWidth);
        turtle.goTo(origin);
        turtle.setHeading(0);
        turtle.penDown();
        turtle.delay = delay;

        drawShape(turtle);
    }

    /**
     * Makes a new shape with a different color.
     *
     * @param color The color to use
     * @return A copied shape with a new color
     */
    @Contract("_ -> new")
    public T withColor(Color color) {
        var cp = copy();
        cp.color = color;
        return cp;
    }

    /**
     * Makes a new shape with a different stroke width.
     *
     * @param strokeWidth The stroke width to use
     * @return A copied shape with a new stroke width
     */
    @Contract("_ -> new")
    public T withStrokeWidth(double strokeWidth) {
        var cp = copy();
        cp.strokeWidth = strokeWidth;
        return cp;
    }

    /**
     * Makes a new shape with a different origin.
     *
     * @param origin The origin to use
     * @return A copied shape with a new origin
     */
    @Contract("_ -> new")
    public T withOrigin(Point2D origin) {
        var cp = copy();
        cp.origin = origin;
        return cp;
    }

    /**
     * Creates a copy of this object.
     *
     * @return An identical object
     */
    @NotNull
    @Contract(" -> new")
    public abstract T copy();

    /**
     * Draws this object using the provided turtle.
     * This method should not change any drawing parameters such as color.
     *
     * @param turtle The turtle to draw with
     */
    protected abstract void drawShape(Turtle turtle);
}
