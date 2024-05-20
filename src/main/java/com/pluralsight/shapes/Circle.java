/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.shapes;

import com.pluralsight.drawing.*;
import manifold.ext.props.rt.api.*;

/**
 * Represents a circle with a specific radius.
 * This shape's origin is in its center.
 */
public final class Circle extends Shape<Circle> {
    @var
    public double radius = 1;

    /**
     * Mutates this circle to use a different radius.
     *
     * @param radius The radius to use
     * @return {@code this}
     */
    public Circle withRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    protected void drawShape(Turtle turtle) {
        throw new RuntimeException("Not implemented");
    }
}
