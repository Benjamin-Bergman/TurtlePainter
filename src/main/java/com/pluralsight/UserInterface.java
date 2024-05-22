/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.drawing.*;
import com.pluralsight.shapes.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class UserInterface {

    private static boolean savePainting(File path, Painting p) {
        try (FileOutputStream fs = new FileOutputStream(path);
             ObjectOutputStream os = new ObjectOutputStream(fs)) {

            os.writeInt(p.width);
            os.writeInt(p.height);
            os.writeObject(p.shapes);

            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    private static Painting loadPainting(File path) {
        try (FileInputStream fs = new FileInputStream(path);
             ObjectInputStream os = new ObjectInputStream(fs)
        ) {
            var width = os.readInt();
            var height = os.readInt();
            var in = os.readObject();

            if (!(in instanceof List<?> list))
                return null;

            List<Shape<?>> shapeList = new ArrayList<>(list.size());
            for (Object e : list) {
                if (!(e instanceof Shape<?> shape))
                    return null;
                shapeList.add(shape);
            }

            var world = new World();
            var turtle = new Turtle(world);

            return new Painting(width, height, shapeList, world, turtle);
        } catch (IOException unused) {
            return null;
        }
    }

    private static Painting createPainting(int width, int height) {
        var world = new World(width, height);
        var turtle = new Turtle(world);

        return new Painting(width, height, List.of(), world, turtle);
    }

    private static <T> T repeat(Supplier<? extends T> input, Predicate<? super T> validator, Consumer<? super T> onFailure) {
        while (true) {
            var val = input.get();
            if (validator.test(val))
                return val;
            onFailure.accept(val);
        }
    }

    private static OptionalInt tryReadInt(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            loop:
            while (true) {
                System.out.println("""
                    --COMMANDS--
                    exit
                    load
                    new""");
                var input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "exit" -> {
                        System.out.println("Goodbye!");
                        break loop;
                    }
                    case "load" -> {
                        System.out.println("Which painting? (Enter nothing to cancel)");
                        var inFile = repeat(
                            () -> new File(scanner.nextLine()),
                            file -> file.exists() || file.getPath().isEmpty(),
                            file -> System.out.println("That file (${file.getPath()}) doesn't exist. Try again."));
                        if (inFile.getPath().isEmpty())
                            break;
                        var loaded = loadPainting(inFile);
                        if (loaded == null) {
                            System.out.println("Something went wrong loading the file. Please try again.");
                            break;
                        }
                        runEditor(loaded, inFile, scanner);
                    }
                    case "new" -> {
                        System.out.println("Width:");
                        var width = repeat(() -> tryReadInt(scanner.nextLine()),
                            OptionalInt::isPresent,
                            v -> System.out.println("Bad input. Try again."))
                            .asInt;
                        System.out.println("Height:");
                        var height = repeat(() -> tryReadInt(scanner.nextLine()),
                            OptionalInt::isPresent,
                            v -> System.out.println("Bad input. Try again."))
                            .asInt;

                        runEditor(createPainting(width, height), null, scanner);
                    }
                    default -> System.out.println("Unknown command \"$input\". Please try again.");
                }
            }
        }
    }

    private void runEditor(Painting painting, File file, Scanner scanner) {

    }
}
