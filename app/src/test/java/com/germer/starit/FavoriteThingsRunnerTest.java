package com.germer.starit;

import com.germer.starit.model.Category;
import com.germer.starit.parser.FavoriteThingsParser;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

class FavoriteThingsRunnerTest {

    private static final Supplier<InputStream> DUMMY_INPUT_STREAM_SUPPLIER = () -> null;

    private static final Category ANIMALS;
    private static final Category COLORS;

    private ByteArrayOutputStream stdOutput;
    private ByteArrayOutputStream stdError;

    private FavoriteThingsRunner sut_FavoriteThingsRunner;
    private FavoriteThingsParser favoriteThingsParserMock;

    static {
        COLORS = new Category("Colors");
        COLORS.addFavoriteThing("red", 1);
        COLORS.addFavoriteThing("brown", 4);
        COLORS.addFavoriteThing("green", 6);

        ANIMALS = new Category("Animals");
        ANIMALS.addFavoriteThing("bear", 7);
        ANIMALS.addFavoriteThing("wolf", 5);
        ANIMALS.addFavoriteThing("squirrel", 3);
    }

    @BeforeEach
    public void setupEach() {
        stdOutput = new ByteArrayOutputStream();
        stdError = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdOutput));
        System.setErr(new PrintStream(stdError));

        sut_FavoriteThingsRunner = Mockito.spy(new FavoriteThingsRunner(DUMMY_INPUT_STREAM_SUPPLIER));
        favoriteThingsParserMock = Mockito.mock(FavoriteThingsParser.class);
        Mockito.doReturn(favoriteThingsParserMock).when(sut_FavoriteThingsRunner).parseSuppliedInputStream();
    }

    @Test
    @DisplayName("Execution with no errors")
    void run_NoErrors() {

        // GIVEN
        Mockito.when(favoriteThingsParserMock.getParsedCategories()).thenReturn(Lists.newArrayList(COLORS, ANIMALS));
        Mockito.when(favoriteThingsParserMock.hasErrors()).thenReturn(false);
        Mockito.when(favoriteThingsParserMock.getParsingErrors()).thenReturn(Collections.emptyList());

        // WHEN
        sut_FavoriteThingsRunner.run();

        // THEN
        assertThat(getStdOutAsString(), equalToCompressingWhiteSpace(
                "Highest rating category: Animals; thing: bear; rating: 7\n" +
                        "Category with highest sum: Animals; sum was 15\n"
        ));
        assertThat(getStdErrAsString(), is(emptyString()));
    }

    @Test
    @DisplayName("Execution with a couple of errors")
    void run_withErrors() {

        // GIVEN
        Mockito.when(favoriteThingsParserMock.getParsedCategories()).thenReturn(Lists.newArrayList(COLORS, ANIMALS));
        Mockito.when(favoriteThingsParserMock.hasErrors()).thenReturn(true);
        Mockito.when(favoriteThingsParserMock.getParsingErrors()).thenReturn(Arrays.asList(
                "Here is one error!", "And here is another one!"
        ));

        // WHEN
        sut_FavoriteThingsRunner.run();

        // THEN
        assertThat(getStdOutAsString(), equalToCompressingWhiteSpace(
                "Highest rating category: Animals; thing: bear; rating: 7\n" +
                        "Category with highest sum: Animals; sum was 15\n"
        ));
        assertThat(getStdErrAsString(), equalToCompressingWhiteSpace(
                "A total of 2 error(s) occurred while parsing: \n" +
                        "Here is one error!\n" +
                        "And here is another one!\n"
        ));
    }

    @Test
    @DisplayName("Execution with a critical exception thrown")
    void run_withFatalError() {
        Mockito.doThrow(new IllegalArgumentException("Very bad error!"))
                .when(sut_FavoriteThingsRunner).parseSuppliedInputStream();

        // WHEN
        sut_FavoriteThingsRunner.run();

        // THEN
        assertThat(getStdOutAsString(), is(emptyString()));
        assertThat(getStdErrAsString(), equalToCompressingWhiteSpace(
                "A fatal error has occurred:\nVery bad error!"
        ));
    }


    private String getStdOutAsString() {
        return stdOutput.toString();
    }

    private String getStdErrAsString() {
        return stdError.toString();
    }


}