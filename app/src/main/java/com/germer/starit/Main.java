package com.germer.starit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Main {

    private static final String CAT1 = "Colors";
    private static final String CAT2 = "Foods";
    private static final String CAT3 = "Dogs";

    public static void main(String[] args) {
        try {
            // Load the data from a file, map is category to item (name/rating)
            Map<String, List<Pair<String, Integer>>> f = null;
            BufferedInputStream bis = new BufferedInputStream(ClassLoader.getSystemResourceAsStream("favorite-things.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) { // for each line, read into the map
                // check which category
                int ic = line.indexOf(":");
                String c = null;
                String after = line.substring(0, ic);
                int i1 = after.indexOf(CAT1);
                int i2 = after.indexOf(CAT2);
                int i3 = after.indexOf(CAT3);
                if (i1 >= 0) {
                    // it's colors!
                    c = "Colors";
                }
                if (i2 >= 0) {
                    // it's foods!
                    c = "Foods";
                }
                if (i3 >= 0) {
                    // it's dogs!
                    c = "Dogs";
                }
                if (i1 < 0 && i2 < 0 && i3 < 0) {
                    c = null; // we didn't find a category
                }
                if (c == null) {
                    System.out.println("Unknown category, cannot process");
                }
                // read the rest of the line
                ic++; // go to the next character after the colon
                String rest = line.substring(ic);
                String[] split = rest.split(","); // split by comma
                for (int i = 0; i < split.length; i++) {
                    String s = split[i];
                    int id = -1;
                    for (int j = 0; j < s.length(); j++) {
                        char ch = s.charAt(j);
                        if (Character.isDigit(ch)) {
                            // stop, we found the digit
                            id = j;
                            break;
                        }
                    }
                    String fav = s.substring(0, id);
                    Integer rat = new Integer(s.substring(id));
                    if (f == null) {
                        f = new HashMap<>();
                    }
                    List<Pair<String, Integer>> existing = null;
                    if (c != null && f.containsKey(c)) {
                        existing = f.get(c);
                    } else {
                        existing = new ArrayList<>();
                        f.put(c, existing);
                    }
                    existing.add(ImmutablePair.of(fav, rat));
                }
            }

            // Find the favorite with the highest rating
            String highestCat = null;
            Pair<String, Integer> highestThing = null;
            List<Pair<String, Integer>> result = f.get(CAT1);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    if (highestThing == null || oneResult.getRight() > highestThing.getRight()) {
                        highestThing = oneResult;
                        highestCat = CAT1;
                    }
                }
            }
            result = f.get(CAT2);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    if (highestThing == null || oneResult.getRight() > highestThing.getRight()) {
                        highestThing = oneResult;
                        highestCat = CAT2;
                    }
                }
            }
            result = f.get(CAT3);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    if (highestThing == null || oneResult.getRight() > highestThing.getRight()) {
                        highestThing = oneResult;
                        highestCat = CAT3;
                    }
                }
            }
            System.out.println(String.format("Highest rating category: %s; thing: %s; rating: %d", highestCat, highestThing.getLeft(), highestThing.getRight()));

            // Find the category with the highest sum of ratings
            highestCat = null;
            int[] sums = new int[3];
            result = f.get(CAT1);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    sums[0] = sums[0] + oneResult.getRight();
                }
            }
            result = f.get(CAT2);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    sums[1] = sums[1] + oneResult.getRight();
                }
            }
            result = f.get(CAT3);
            if (result != null) {
                for (Pair<String, Integer> oneResult : result) {
                    sums[2] = sums[2] + oneResult.getRight();
                }
            }

            // find highest index
            int highestIndex = -1;
            int highestSum = -1;
            for (int i = 0; i < sums.length; i++) {
                if (sums[i] > highestSum) {
                    highestIndex = i;
                    highestSum = sums[i];
                }
            }
            // which category is the index?
            switch (highestIndex) {
                case 0:
                    highestCat = CAT1;
                    break;
                case 1:
                    highestCat = CAT2;
                    break;
                case 2:
                    highestCat = CAT3;
                    break;
                default:
                    br.close();
                    throw new IllegalArgumentException("Unknown cat");
            }
            System.out.println(String.format("Category with highest sum: %s; sum was %d", highestCat, sums[highestIndex]));

            br.close();
        } catch (Exception e) {
            System.out.println("An error occurred");
        }
    }
}