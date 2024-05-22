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
import java.util.concurrent.locks.*;

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
    private final AtomicInteger currentRun = new AtomicInteger(0);

    private final Lock runLock = new ReentrantLock();
    private final Condition runSleep = runLock.newCondition();
    private boolean running;
    private boolean added;

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
        this.shapes = Collections.synchronizedList(new ArrayList<>(shapes));
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
        draw(currentRun.incrementAndGet(), null);
    }

    /**
     * Adds a shape to this painting and draws it.
     *
     * @param shape The shape to add
     */
    public void add(Shape<?> shape) {
        runLock.lock();
        try {
            shapes.add(shape);
            if (running) added = true;
            else new Thread(() -> draw(currentRun.incrementAndGet(), shapes.size() - 1)).start();
        } finally {
            runLock.unlock();
        }
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

    private void draw(int run, Integer startAt) {
        runLock.lock();
        try {
            while (running) runSleep.await();
            running = true;
            added = false;
        } finally {
            runLock.unlock();
        }

        if (currentRun.getAcquire() != run) {
            runLock.lock();
            try {
                running = false;
                runSleep.signalAll();
                return;
            } finally {
                runLock.unlock();
            }
        }

        if (startAt == null)
            world.resizeWorld(width, height);
        //noinspection ReassignedVariable
        int i = Optional.ofNullable(startAt).orElse(0);
        while (true) {
            //noinspection ForLoopWithMissingComponent
            for (; i < shapes.size(); i++) {
                Shape<?> shape = shapes.get(i);
                shape.draw(turtle);
                if (currentRun.getAcquire() != run) {
                    runLock.lock();
                    try {
                        running = false;
                        runSleep.signalAll();
                        return;
                    } finally {
                        runLock.unlock();
                    }
                }
            }

            runLock.lock();
            try {
                if (!added) {
                    turtle.penUp();
                    turtle.goTo(world.width + 100, world.height + 100);

                    running = false;
                    runSleep.signalAll();
                    return;
                }
                added = false;
            } finally {
                runLock.unlock();
            }
        }
    }
}
