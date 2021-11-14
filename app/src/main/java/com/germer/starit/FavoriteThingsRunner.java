package com.germer.starit;

import com.germer.starit.model.Category;
import com.germer.starit.model.FavoriteThing;
import com.germer.starit.parser.FavoriteThingsParser;

import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class centralizes the business logic for the Favorite Things highest-rating listing features. It will
 * use the {@link FavoriteThingsParser} to process the contents from a provided {@link InputStream} and display
 * the results to the default output in the following format:
 *
 * <pre>
 * Highest rating category: Colors; thing: blue; rating: 50
 * Category with highest sum: Foods; sum was 63
 * </pre>
 * <p>
 * Any parsing errors detected by the parser will be sent to the default error output, but the application will be
 * lenient and evaluate the results based on the data that was possible to be parsed, if any.
 */
public class FavoriteThingsRunner {

    private static final int EXIT_ERROR = 1;
    private static final int EXIT_SUCCESS = 0;

    private final Supplier<InputStream> inputStreamSupplier;

    public FavoriteThingsRunner(Supplier<InputStream> inputStreamSupplier) {
        this.inputStreamSupplier = inputStreamSupplier;
    }

    public int run() {
        try {
            FavoriteThingsParser favoriteThingsParser = parseSuppliedInputStream();

            if (favoriteThingsParser.hasErrors()) {
                List<String> parsingErrors = favoriteThingsParser.getParsingErrors();
                System.err.printf("A total of %d error(s) occurred while parsing: %n", parsingErrors.size());
                parsingErrors.forEach(System.err::println);
                System.err.println();
            }

            Collection<Category> parsedCategories = favoriteThingsParser.getParsedCategories();

            if (parsedCategories.isEmpty()) {
                System.out.println("No favorite things found in any category, nothing to see here.");
            } else {
                displayHighestRatingThing(parsedCategories);
                displayHighestRatingCategory(parsedCategories);
            }

            return EXIT_SUCCESS;
        } catch (Exception e) {
            System.err.printf("A fatal error has occurred:%n%s", e.getMessage());
            return EXIT_ERROR;
        }
    }

    protected FavoriteThingsParser parseSuppliedInputStream(){
        Set<String> validCategoryNames = ConfigLoader.loadValidCategoryNames();
        FavoriteThingsParser favoriteThingsParser = new FavoriteThingsParser(validCategoryNames);
        favoriteThingsParser.parse(inputStreamSupplier.get());
        return favoriteThingsParser;
    }

    private void displayHighestRatingThing(Collection<Category> parsedCategories) {
        parsedCategories.stream()
                .map(Category::getHighestRatingFavoriteItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparing(FavoriteThing::getRating))
                .ifPresent(highestRatingFavoriteThing ->
                        System.out.printf("Highest rating category: %s; thing: %s; rating: %d%n",
                                highestRatingFavoriteThing.getCategoryName(),
                                highestRatingFavoriteThing.getName(),
                                highestRatingFavoriteThing.getRating())
                );
    }

    private void displayHighestRatingCategory(Collection<Category> parsedCategories) {
        parsedCategories.stream()
                .max(Comparator.comparing(Category::getTotalScore))
                .ifPresent(highestRatingCategory ->
                        System.out.printf("Category with highest sum: %s; sum was %d%n",
                                highestRatingCategory.getName(), highestRatingCategory.getTotalScore())
                );
    }
}
