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
    /**
     * The width of this painting.
     */
    @val
    public final int width;
    /**
     * The height of this painting.
     */
    @val
    public final int height;
    /**
     * The canvas this painting draws to.
     */
    @var
    @set(PropOption.Private)
    public final transient World world;
    /**
     * The turtle this painting draws with.
     */
    @var
    @set(PropOption.Private)
    public final transient Turtle turtle;
    @val
    private final List<Shape<?>> shapes;
    private final transient BlockingQueue<Optional<Shape<?>>> queue = new LinkedBlockingQueue<>();
    private transient Thread thread;

    /**
     * @param width  The width in pixels
     * @param height The height in pixels
     * @param shapes The shapes making up the painting
     * @param world  The canvas to draw to
     * @param turtle The turtle to draw with
     */
    public Painting(int width, int height, Collection<Shape<?>> shapes, World world, Turtle turtle) {
        this.width = width;
        this.height = height;
        this.shapes = shapes
            .stream()
            .map(Shape::copy)
            .collect(Collectors.toCollection(ArrayList::new));
        this.world = world;
        this.turtle = turtle;
        thread = null;
    }

    /**
     * Sets the tools this painting will use to draw.
     * Redraws the painting if applicable.
     *
     * @param world  The canvas to draw on
     * @param turtle The turtle to draw with
     */
    public void setCanvas(World world, Turtle turtle) {
        this.world = world;
        this.turtle = turtle;
        if (thread != null)
            draw();
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
        if (thread == null)
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

        if (thread == null) draw();
        else queue.put(Optional.of(cshape));
    }

    /**
     * Removes a shape from this painting and redraws it.
     *
     * @param index The index of the item to remove
     */
    public void remove(int index) {
        var removed = shapes.remove(index);

        if (thread == null || !queue.removeIf(op -> op.isPresent() && op.get().UUID == removed.UUID))
            draw();
    }

    /**
     * Stops the render thread drawing this painting.
     */
    public void stop() {
        if (thread == null)
            return;
        queue.clear();
        thread.interrupt();
        thread = null;
    }

    private void start() {
        thread = new Thread(this::runDrawQueue);
        thread.start();
    }

    private void runDrawQueue() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted)
                    return;
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
        } catch (InterruptedException ignored) {
            turtle.penUp();
            turtle.goTo(width + 100, height + 100);
        }
    }
}
