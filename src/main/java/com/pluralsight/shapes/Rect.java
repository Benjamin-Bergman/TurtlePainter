/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;

/**
 * Represents a rectangle with a specific width and height.
 * This shape's origin is in its upper-left corner.
 */
public final class Rect extends Shape<Rect> {
    /**
     * The width of this rectangle.
     */
    @var
    public double width = 1;
    /**
     * The height of this rectangle.
     */
    @var
    public double height = 1;

    /**
     * Mutates this rect to use a different width.
     *
     * @param width The width to use
     * @return {@code this}
     */
    public Rect withWidth(double width) {
        this.width = width;
        return this;
    }

    /**
     * Mutates this rect to use a different height.
     *
     * @param height The height to use
     * @return {@code this}
     */
    public Rect withHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    protected void drawShape(Turtle turtle) {
        turtle.forward(width);
        turtle.turnRight(90);
        turtle.forward(height);
        turtle.turnRight(90);
        turtle.forward(width);
        turtle.turnRight(90);
        turtle.forward(height);
    }
}
