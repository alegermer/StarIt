package com.germer.starit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class FavoriteThing implements Comparable<FavoriteThing> {

    private final String categoryName;
    private final String name;
    private final int rating;

    public static FavoriteThing of(String categoryName, String name, int rating) {
        return new FavoriteThing(categoryName, name, rating);
    }

    @Override
    public int compareTo(FavoriteThing other) {
        return Integer.compare(this.rating, other.rating);
    }
}
