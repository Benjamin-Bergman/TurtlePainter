/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;

import java.awt.*;
import java.awt.geom.*;

/**
 * Represents a shape which can be drawn with a {@link Turtle}.
 *
 * @param <T> The implementing class
 */
public abstract class Shape<T extends Shape<T>> implements TurtleDrawable {
    /**
     * The color used to draw this shape.
     */
    @var
    public final Color color = Color.BLACK;
    /**
     * The width of stroke to use when drawing this shape.
     */
    @var
    public final double strokeWidth = 1;
    /**
     * The origin point for this shape.
     */
    @var
    public final Point2D origin = new Point2D.Double(0, 0);

    @Override
    public final void draw(Turtle turtle) {
        turtle.penUp();
        turtle.setColor(color);
        turtle.setPenWidth(strokeWidth);
        turtle.goTo(origin);
        turtle.setHeading(0);
        turtle.penDown();

        drawShape(turtle);
    }

    /**
     * Mutates this shape to use a different color.
     *
     * @param color The color to use
     * @return {@code this}
     */
    public T withColor(Color color) {
        this.color = color;
        return (T) this;
    }

    /**
     * Mutates this shape to use a different stroke width.
     *
     * @param strokeWidth The stroke width to use
     * @return {@code this}
     */
    public T withStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        return (T) this;
    }

    /**
     * Mutates this shape to use a different origin.
     *
     * @param origin The origin to use
     * @return {@code this}
     */
    public T withOrigin(Point2D origin) {
        this.origin = origin;
        return (T) this;
    }

    /**
     * Draws this object using the provided turtle.
     * This method should not change any drawing parameters such as color.
     *
     * @param turtle The turtle to draw with
     */
    protected abstract void drawShape(Turtle turtle);
}
