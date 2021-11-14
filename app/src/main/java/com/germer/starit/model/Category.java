package com.germer.starit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

/**
 * A Category represents a group of {@link FavoriteThing}s. This class offers convenience methods to query existing
 * {@link FavoriteThing}s by name and to efficiently access the {@link FavoriteThing}s ordered by their ratings.
 */
@ToString
@EqualsAndHashCode
public class Category {

    @Getter
    private final String name;
    @Getter
    private int totalScore = 0;
    private final Set<FavoriteThing> favoriteThings = new TreeSet<>(Comparator.reverseOrder());
    private final Map<String, FavoriteThing> favoriteThingByName = new HashMap<>();

    public Category(String name) {
        this.name = name;
    }

    /**
     * @param name   Name of the new {@link FavoriteThing} to be added.
     * @param rating Rating of the new {@link FavoriteThing} to be added.
     * @throws IllegalArgumentException in case a the {@link FavoriteThing}'s name is already
     *                                  present in this {@link Category}.
     */
    public void addFavoriteThing(String name, int rating) {
        addFavoriteThing(FavoriteThing.of(this.getName(), name, rating));
    }

    /**
     * @param favoriteThing The {@link FavoriteThing} to be added.
     * @throws IllegalArgumentException in case a the {@link FavoriteThing}'s name is already
     *                                  present in this {@link Category}.
     */
    protected void addFavoriteThing(FavoriteThing favoriteThing) {
        if (favoriteThingByName.containsKey(favoriteThing.getName())) {
            throw new IllegalArgumentException(String.format(
                    "This Category already contains a FavoriteThing named '%s'.", favoriteThing.getName()
            ));
        }

        favoriteThingByName.put(favoriteThing.getName(), favoriteThing);
        favoriteThings.add(favoriteThing);
        totalScore += favoriteThing.getRating();
    }

    /**
     * @param name The name of the {@link FavoriteThing} to be removed, case-sensitive.
     * @return The removed {@link FavoriteThing}, or null in case it didn't exist.
     */
    public FavoriteThing removeFavoriteThingByName(String name) {
        FavoriteThing removedItem = favoriteThingByName.remove(name);
        if (removedItem != null) {
            favoriteThings.remove(removedItem);
            totalScore -= removedItem.getRating();
        }
        return removedItem;
    }

    public Optional<FavoriteThing> getFavoriteThingByName(String name) {
        return Optional.ofNullable(favoriteThingByName.get(name));
    }

    /**
     * @return An {@link Iterable} of {@link FavoriteThing}s ordered by rating, from the highest to
     * the lowest one.
     */
    public Iterable<FavoriteThing> getFavoriteThingsOrderedByRating() {
        return Collections.unmodifiableSet(favoriteThings);
    }

    /**
     * @return The highest rated {@link FavoriteThing} on this {@link Category}
     * @see #getFavoriteThingsOrderedByRating()
     */
    public Optional<FavoriteThing> getHighestRatingFavoriteItem() {
        return favoriteThings.stream().findFirst();
    }

    /**
     * @return The amount of {@link FavoriteThing}s linked to this {@link Category}
     */
    public int size() {
        return favoriteThings.size();
    }
}
