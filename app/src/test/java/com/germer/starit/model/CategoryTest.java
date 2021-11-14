package com.germer.starit.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    @DisplayName("It fails on duplicated Favorite Things")
    public void addFavoriteThing_failsOnDuplicate() {
        // GIVEN
        Category animals = new Category("Animals");
        animals.addFavoriteThing("owl", 1);

        // WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> animals.addFavoriteThing("owl", 2)
        );

        // THEN
        Assertions.assertEquals("This Category already contains a FavoriteThing named 'owl'.", exception.getMessage());
    }

    @Test
    @DisplayName("It should return highest rating favorite thing")
    public void getHighestRatingFavoriteThing() {
        // GIVEN
        Category animals = new Category("Animals");
        animals.addFavoriteThing("owl", 5);
        animals.addFavoriteThing("bear", 7);
        animals.addFavoriteThing("wolf", 4);
        animals.addFavoriteThing("deer", 2);

        // WHEN
        Optional<FavoriteThing> highestRatingFavoriteItem = animals.getHighestRatingFavoriteItem();

        // THEN
        Assertions.assertEquals(4, animals.size());
        Assertions.assertTrue(highestRatingFavoriteItem.isPresent());
        assertEquals(FavoriteThing.of("Animals", "bear", 7), highestRatingFavoriteItem.get());
    }

}