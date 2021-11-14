package com.germer.starit.parser;

import com.germer.starit.model.Category;
import com.germer.starit.model.FavoriteThing;
import com.google.common.annotations.VisibleForTesting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Parser for the Favorite-Things schema. Used to parse source input-streams into sets of {@link FavoriteThing}s
 * grouped in {@link Category} instances.
 * <p>
 * This is the Favorite-Things schema syntax (note FT is short for Favorite-Thing):
 * <pre>
 * &lt;CategoryName&gt;:&lt;FT1Name&gt;&lt;FT1IntegerRating&gt;&lt;FT2Name&gt;&lt;FT2IntegerRating&gt,...
 * </pre>
 * <b>Example:</b>
 * <pre>
 * MyCategory:FavThing5,FavThing1,FavThing10
 * </pre>
 * One category by line.
 * </p>
 * <p>In case the provided {@link InputStream} is readable, this parser will take a lenient approach and collect
 * as many {@link FavoriteThing} and {@link Category} instances that are found to be respecting the expected syntax.
 * Any errors or duplicate entries will yield error messages which will be available through {@link #getParsingErrors()}
 * after the processing.
 * </p>
 */
public class FavoriteThingsParser {

    private static final Pattern CATEGORY_TITLE_AND_ITEMS = Pattern.compile("^([^:]+):(.*)$", Pattern.DOTALL);
    private static final Pattern CATEGORY_ITEM_AND_SCORE = Pattern.compile("^([^\\d]+)(\\d+)$");

    private final Set<String> validCategoryNames;
    private final List<String> errors = new ArrayList<>();
    private final Map<String, Category> categoryByName = new HashMap<>();

    public FavoriteThingsParser(Set<String> validCategoryNames) {
        this.validCategoryNames = validCategoryNames;
    }

    /**
     * Parses the contents of the given {@link InputStream} according to the Favorite-Things schema, the parsed
     * {@link Category} instances containing the {@link FavoriteThing} will be later available through the
     * {@link #getParsedCategories()} and any non-critical parsing errors can be verified through the
     * {@link #getParsingErrors()}.
     *
     * @param inputStream The {@link InputStream} to the source Favorite-Things contents.
     */
    public void parse(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int lineNumber = 0;
            while (reader.ready()) {
                String line = reader.readLine();
                parseCategoryFromLine(++lineNumber, line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process the provided favorite things source!", e);
        }
    }

    private void parseCategoryFromLine(int lineNumber, String line) {
        Matcher matcher = CATEGORY_TITLE_AND_ITEMS.matcher(line);

        if (matcher.matches()) {
            String categoryName = matcher.group(1).trim();
            String items = matcher.group(2);

            if (validCategoryNames.contains(categoryName)) {
                Category category = categoryByName.computeIfAbsent(categoryName, Category::new);
                parseAndPopulateFavoriteItems(lineNumber, category, items);
            } else {
                addError("Unknown category '%s' on line %d, this line was skipped.", categoryName, lineNumber);
            }

        } else {
            addError("Invalid syntax in line %d, this line was skipped.", lineNumber);
        }
    }

    private void parseAndPopulateFavoriteItems(int lineNumber, Category category, String items) {
        Arrays.stream(items.split(","))
                .forEach(item -> parseAndAddFavoriteItem(lineNumber, category, item));
    }

    private void parseAndAddFavoriteItem(int lineNumber, Category category, String item) {
        Matcher matcher = CATEGORY_ITEM_AND_SCORE.matcher(item);
        if (matcher.matches()) {
            String name = matcher.group(1);
            int score = Integer.parseInt(matcher.group(2));

            Optional<FavoriteThing> existingItem = category.getFavoriteThingByName(name);
            if (existingItem.isPresent()) {
                addError("The score %d present on line %d for the item '%s' in the category '%s' " +
                                "was ignored because there is already a previous score of %s set.",
                        score, lineNumber, name, category.getName(), existingItem.get().getRating());
            } else {
                category.addFavoriteThing(name, score);
            }
        } else {
            addError("Invalid syntax for item '%s' of category '%s' on line %d.", item, category.getName(), lineNumber);
        }
    }

    private void addError(String format, Object... params) {
        errors.add(String.format(format, params));
    }

    /**
     * @return <code>true</code> if a previous execution of {@link #parse(InputStream)} yielded any non-critical
     * parsing errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * @return All non-critical errors raised so far through the {@link #parse(InputStream)} executions.
     */
    public List<String> getParsingErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * @return All parsed {@link Category} so far containing the respective {@link FavoriteThing}s.
     */
    public Collection<Category> getParsedCategories() {
        return Collections.unmodifiableCollection(categoryByName.values());
    }

    @VisibleForTesting
    protected Category getParsedCategoryByName(String name){
        return categoryByName.get(name);
    }

}
