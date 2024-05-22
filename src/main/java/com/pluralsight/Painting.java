/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.*;
import manifold.ext.props.rt.api.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Represents a painting.
 */
public final class Painting implements Serializable {
    @val
    public final int width;
    @val
    public final int height;
    @val
    public final World world;
    @val
    public final Turtle turtle;
    @val
    private final List<Shape<?>> shapes;
    private final transient BlockingQueue<Optional<Shape<?>>> queue = new LinkedBlockingQueue<>();
    private transient boolean hasStarted;

    /**
     * @param width  The width in pixels
     * @param height The height in pixels
     * @param shapes The shapes making up the painting
     * @param world  The canvas to draw to
     * @param turtle The turtle to draw with
     */
    public Painting(int width, int height, List<Shape<?>> shapes, World world, Turtle turtle) {
        this.width = width;
        this.height = height;
        this.shapes = shapes
            .stream()
            .map(Shape::copy)
            .collect(Collectors.toCollection(ArrayList::new));
        this.world = world;
        this.turtle = turtle;
        hasStarted = false;
    }

    /**
     * Draws this painting to the given World using the given Turtle.
     * This will clear the canvas first.
     * This operation is asynchronous.
     */
    public void draw() {
        queue.clear();
        queue.put(Optional.empty());
        shapes.forEach(shape -> queue.put(Optional.of(shape)));
        if (!hasStarted)
            start();
    }

    /**
     * Adds a shape to this painting and draws it.
     *
     * @param shape The shape to add
     */
    public void add(Shape<?> shape) {
        var cshape = shape.copy();
        shapes.add(cshape);

        if (hasStarted) queue.put(Optional.of(cshape));
        else draw();
    }

    /**
     * Removes a shape from this painting and redraws it.
     *
     * @param index The index of the item to remove
     */
    public void remove(int index) {
        var removed = shapes.remove(index);

        if (!hasStarted || !queue.removeIf(op -> op.isPresent() && op.get().UUID == removed.UUID))
            draw();
    }

    private void start() {
        hasStarted = true;
        new Thread(this::runDrawQueue).start();
    }

    private void runDrawQueue() {
        while (true) {
            //noinspection ReassignedVariable
            var command = queue.poll();
            //noinspection OptionalAssignedToNull
            if (command == null) {
                turtle.penUp();
                turtle.goTo(width + 100, height + 100);
                command = queue.take();
            }
            if (command.isPresent())
                command.get().draw(turtle);
            else
                world.resizeWorld(width, height);
        }
    }
}
