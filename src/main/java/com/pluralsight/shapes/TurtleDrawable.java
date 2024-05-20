/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;

/**
 * Represents something which can be drawn using a {@link Turtle}.
 */
@FunctionalInterface
public interface TurtleDrawable {
    /**
     * Draws this object using the provided turtle.
     *
     * @param turtle The turtle to draw with
     */
    void draw(Turtle turtle);
}
