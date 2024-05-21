/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;
import org.jetbrains.annotations.*;

/**
 * Represents a rectangle with a specific width and height.
 * This shape's origin is in its upper-left corner.
 */
public final class Rect extends Shape<Rect> {
    /**
     * The width of this rectangle.
     */
    @var
    @set(PropOption.Private)
    public double width = 1;
    /**
     * The height of this rectangle.
     */
    @var
    @set(PropOption.Private)
    public double height = 1;

    /**
     * Creates a new Rect with default values.
     */
    public Rect() {
    }

    private Rect(Rect copied) {
        super(copied);
        width = copied.width;
        height = copied.height;
    }

    /**
     * Makes a new rect with a different width.
     *
     * @param width The width to use
     * @return A copied rect with a new width
     */
    @Contract("_ -> new")
    public Rect withWidth(double width) {
        var cp = copy();
        cp.width = width;
        return cp;
    }

    /**
     * Makes a new rect with a different height.
     *
     * @param height The height to use
     * @return A copied rect with a new height
     */
    @Contract("_ -> new")
    public Rect withHeight(double height) {
        var cp = copy();
        cp.height = height;
        return cp;
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

    @Override
    protected @NotNull Rect copy() {
        return new Rect(this);
    }
}
