package com.germer.starit;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple static class which only purpose is to load the valid categories from the "categories.conf" file expected
 * to be present in the classpath resources.
 */
public class ConfigLoader {

    public static final String CATEGORIES_CONF_FILE = "categories.conf";

    public static Set<String> loadValidCategoryNames() {
        InputStream categoriesConf = ClassLoader.getSystemResourceAsStream(CATEGORIES_CONF_FILE);

        if (categoriesConf == null) {
            throw new IllegalStateException(String.format(
                    "Could not find the %s file in the resources classpath.", CATEGORIES_CONF_FILE)
            );
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(categoriesConf))) {

            return reader.lines()
                    .flatMap(line -> Arrays.stream(line.split(":")))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("Could not load the valid categories from the configuration file %s. Details: %s",
                            CATEGORIES_CONF_FILE, e.getMessage(), e)
            );
        }
    }
}
