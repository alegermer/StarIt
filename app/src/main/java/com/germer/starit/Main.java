package com.germer.starit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    private static final String FAVORITE_THINGS_RESOURCE = "favorite-things.txt";

    public static void main(String[] args) {

        FavoriteThingsRunner favoriteThingsRunner;
        if (args.length == 0) {
            System.out.println("No favorite things source file specified, using sample one.");
            favoriteThingsRunner = new FavoriteThingsRunner(Main::readFromResources);
        } else {
            favoriteThingsRunner = new FavoriteThingsRunner(() -> readFromFile(args[0]));
        }

        System.exit(favoriteThingsRunner.run());
    }

    /**
     * @return The {@link InputStream} to the internal classpath resource file {@value FAVORITE_THINGS_RESOURCE} as
     * the previous version of the application used to run.
     */
    public static InputStream readFromResources() {
        return ClassLoader.getSystemResourceAsStream(FAVORITE_THINGS_RESOURCE);
    }

    /**
     * @param filePath The path to the file from which favorite things should be read
     * @return The {@link InputStream} to the specified file. This will throw runtime exceptions in case of an error
     * finding or opening the file.
     */
    public static InputStream readFromFile(String filePath) {

        File sourceFile = new File(filePath);
        if (!sourceFile.isFile() || !sourceFile.canRead()) {
            throw new IllegalArgumentException(
                    String.format("The specified file %s is not a valid file or cannot be read.", filePath)
            );
        }

        try {
            return new FileInputStream(sourceFile);
        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("Failed to read the specified file %s: %s", filePath, e.getMessage())
                    , e);
        }
    }

}