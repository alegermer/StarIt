package com.germer.starit.parser;

import com.germer.starit.model.Category;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FavoriteThingsParserTest {

    @Test
    @DisplayName("It should skip unexpected category and error should be available")
    public void parse_unexpectedCategory() {
        // GIVEN
        FavoriteThingsParser parser = new FavoriteThingsParser(Sets.newHashSet("Colors", "Countries"));
        String favoriteThings = "Colors:blue1,red2\nMammals:Cow2Wolf5\nCountries:Australia3,Canada4,New Zealand5";
        // WHEN
        parser.parse(stringToInputStream(favoriteThings));
        // THEN
        Assertions.assertTrue(parser.hasErrors());
        assertThat(parser.getParsingErrors(), hasSize(1));
        assertThat(parser.getParsingErrors(), hasItems("Unknown category 'Mammals' on line 2, this line was skipped."));
        assertThat(parser.getParsedCategories(), hasSize(2));

        Category colorsCategory = parser.getParsedCategoryByName("Colors");
        assertNotNull(colorsCategory);
        assertEquals(2, colorsCategory.size());
        assertEquals(1, colorsCategory.getFavoriteThingByName("blue").get().getRating());
        assertEquals(2, colorsCategory.getFavoriteThingByName("red").get().getRating());

        Category countriesCategory = parser.getParsedCategoryByName("Countries");
        assertNotNull(countriesCategory);
        assertEquals(3, countriesCategory.size());
        assertEquals(3, countriesCategory.getFavoriteThingByName("Australia").get().getRating());
        assertEquals(4, countriesCategory.getFavoriteThingByName("Canada").get().getRating());
        assertEquals(5, countriesCategory.getFavoriteThingByName("New Zealand").get().getRating());
    }

    @Test
    @DisplayName("It should skip duplicated things and error should be available")
    public void parse_duplicatedFavoriteThings() {
        // GIVEN
        FavoriteThingsParser parser = new FavoriteThingsParser(Sets.newHashSet("Colors", "Countries"));
        String favoriteThings = "Colors:blue1,red2,blue3\nCountries:Australia3,Canada4,New Zealand5\nColors:red5";
        // WHEN
        parser.parse(stringToInputStream(favoriteThings));
        // THEN
        Assertions.assertTrue(parser.hasErrors());
        assertThat(parser.getParsingErrors(), hasSize(2));
        assertThat(parser.getParsingErrors(), hasItems(
                "The score 3 present on line 1 for the item 'blue' in the category 'Colors' " +
                        "was ignored because there is already a previous score of 1 set.",
                "The score 5 present on line 3 for the item 'red' in the category 'Colors' " +
                        "was ignored because there is already a previous score of 2 set."
        ));
        assertThat(parser.getParsedCategories(), hasSize(2));

        Category colorsCategory = parser.getParsedCategoryByName("Colors");
        assertNotNull(colorsCategory);
        assertEquals(2, colorsCategory.size());
        assertEquals(1, colorsCategory.getFavoriteThingByName("blue").get().getRating());
        assertEquals(2, colorsCategory.getFavoriteThingByName("red").get().getRating());

        Category countriesCategory = parser.getParsedCategoryByName("Countries");
        assertNotNull(countriesCategory);
        assertEquals(3, countriesCategory.size());
        assertEquals(3, countriesCategory.getFavoriteThingByName("Australia").get().getRating());
        assertEquals(4, countriesCategory.getFavoriteThingByName("Canada").get().getRating());
        assertEquals(5, countriesCategory.getFavoriteThingByName("New Zealand").get().getRating());
    }

    @Test
    @DisplayName("It should skip invalid line with no Category and error should be available")
    public void parse_invalidLineWithNoCategoryName() {
        // GIVEN
        FavoriteThingsParser parser = new FavoriteThingsParser(Sets.newHashSet("Colors", "Countries"));
        String favoriteThings = "blue1,red2,blue3\nCountries:Australia3,Canada4,New Zealand5";
        // WHEN
        parser.parse(stringToInputStream(favoriteThings));
        // THEN
        Assertions.assertTrue(parser.hasErrors());
        assertThat(parser.getParsingErrors(), hasSize(1));
        assertThat(parser.getParsingErrors(), hasItems(
                "Invalid syntax in line 1, this line was skipped."
        ));
        assertThat(parser.getParsedCategories(), hasSize(1));

        Category countriesCategory = parser.getParsedCategoryByName("Countries");
        assertNotNull(countriesCategory);
        assertEquals(3, countriesCategory.size());
        assertEquals(3, countriesCategory.getFavoriteThingByName("Australia").get().getRating());
        assertEquals(4, countriesCategory.getFavoriteThingByName("Canada").get().getRating());
        assertEquals(5, countriesCategory.getFavoriteThingByName("New Zealand").get().getRating());
    }

    @Test
    @DisplayName("It should skip invalid Favorite Thing without rating and error should be available")
    public void parse_invalidFavoriteThingMissingRating() {
        // GIVEN
        FavoriteThingsParser parser = new FavoriteThingsParser(Sets.newHashSet("Colors", "Countries"));
        String favoriteThings = "Countries:Australia3,Canada,New Zealand5";
        // WHEN
        parser.parse(stringToInputStream(favoriteThings));
        // THEN
        Assertions.assertTrue(parser.hasErrors());
        assertThat(parser.getParsingErrors(), hasSize(1));
        assertThat(parser.getParsingErrors(), hasItems(
                "Invalid syntax for item 'Canada' of category 'Countries' on line 1."
        ));
        assertThat(parser.getParsedCategories(), hasSize(1));

        Category countriesCategory = parser.getParsedCategoryByName("Countries");
        assertNotNull(countriesCategory);
        assertEquals(2, countriesCategory.size());
        assertEquals(3, countriesCategory.getFavoriteThingByName("Australia").get().getRating());
        assertEquals(5, countriesCategory.getFavoriteThingByName("New Zealand").get().getRating());
    }

    @Test
    @DisplayName("It should present no errors when the contents are fully valid")
    public void parse_totallyValidInput() {
        // GIVEN
        FavoriteThingsParser parser = new FavoriteThingsParser(Sets.newHashSet("Colors", "Countries"));
        String favoriteThings = "Colors:blue2,yellow4\nCountries:Australia3,Canada4,New Zealand5";
        // WHEN
        parser.parse(stringToInputStream(favoriteThings));
        // THEN
        Assertions.assertFalse(parser.hasErrors());
        assertThat(parser.getParsingErrors(), hasSize(0));
        assertThat(parser.getParsedCategories(), hasSize(2));

        Category colorsCategory = parser.getParsedCategoryByName("Colors");
        assertNotNull(colorsCategory);
        assertEquals(2, colorsCategory.size());
        assertEquals(2, colorsCategory.getFavoriteThingByName("blue").get().getRating());
        assertEquals(4, colorsCategory.getFavoriteThingByName("yellow").get().getRating());

        Category countriesCategory = parser.getParsedCategoryByName("Countries");
        assertNotNull(countriesCategory);
        assertEquals(3, countriesCategory.size());
        assertEquals(3, countriesCategory.getFavoriteThingByName("Australia").get().getRating());
        assertEquals(4, countriesCategory.getFavoriteThingByName("Canada").get().getRating());
        assertEquals(5, countriesCategory.getFavoriteThingByName("New Zealand").get().getRating());
    }

    private InputStream stringToInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }


}