/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.*;
import manifold.ext.props.rt.api.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

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
    private final List<Shape> shapes;
    private final AtomicInteger currentRun = new AtomicInteger(0);
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * @param width  The width in pixels
     * @param height The height in pixels
     * @param shapes The shapes making up the painting
     * @param world  The canvas to draw to
     * @param turtle The turtle to draw with
     */
    public Painting(int width, int height, List<Shape> shapes, World world, Turtle turtle) {
        this.width = width;
        this.height = height;
        this.shapes = new ArrayList<>(shapes);
        this.world = world;
        this.turtle = turtle;
    }

    /**
     * Draws this painting to the given World using the given Turtle.
     * This will clear the canvas first.
     * This operation is asynchronous.
     */
    public void drawAsync() {
        new Thread(this::draw).start();
    }

    /**
     * Draws this painting to the given World using the given Turtle.
     * This will clear the canvas first.
     */
    public void draw() {
        draw(currentRun.incrementAndGet());
    }

    /**
     * Adds a shape to this painting and draws it.
     *
     * @param shape The shape to add
     */
    public void add(Shape shape) {
        shapes.add(shape);

        // This shouldn't redraw the whole painting,
        // but I don't want to figure out the necessary
        // synchronization to make it do that.
        drawAsync();
    }

    /**
     * Removes a shape from this painting and redraws it.
     *
     * @param index The index of the item to remove
     */
    public void remove(int index) {
        shapes.remove(index);
        drawAsync();
    }

    private void draw(Integer monitor) {
        synchronized (running) {
            while (running.getAndSet(true))
                running.wait(0L, 0);

            if (monitor != null && currentRun.getAcquire() != monitor) {
                running.setRelease(false);
                running.notifyAll();
                return;
            }

            world.resizeWorld(width, height);
            for (int i = 0; i < shapes.size(); i++) {
                Shape shape = shapes.get(i);
                shape.draw(turtle);
                if (monitor != null && currentRun.getAcquire() != monitor)
                    break;
            }
            turtle.penUp();
            turtle.goTo(world.width + 100, world.height + 100);

            running.setRelease(false);
            running.notifyAll();
        }
    }
}
