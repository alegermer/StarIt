# StarIt (My Favorite Things)

There are many ways that we could improve the original code, here I am converting the original implementation into
a more Object-Oriented way, however without introducing more features at a first moment, but providing some minor 
improvements on user-feedback when parsing errors are present. I could have refactored the code into a shorter and
more efficient iterative single-file application, but I thought it would be more interesting to explore some nice
separation of concerns and initial OO modeling for a small application like this.

I didn't change the original non-interactive behaviour, the process still runs in one go, reading from a target
input source and evaluating the highest ratings for the top 1 favorite-thing and category.

The main change in behaviour were:
* The error messages wording was changed, and they are now sent to the error output.
* Any parsing errors that could be ignored are now presented before the top-rating results, but the process still shows
the results even if some errors occurred during parsing.
* The category-naming behavior was kept case-sensitive for now (meaning that "name" and "Name" would yield distinct
unique categories). Duplicated exact category names will be merged as before, however duplicated favorite things will
now be ignored (the first favorite thing within a Category will have its rating prevailing over duplicates).
* For convenience an external source-file for favorite things can now be provided as the 1st argument to the program.

The original Main.java was also broken-down into a gradle project, as if it was in preparation for a larger, more 
maintainable piece of software. The accepted categories are no longer hardcoded, but they are kept on a resource file
called `categories.conf`. The contents of that file are colon or new-line separated category-names. 

The new `Category` class offers a few methods that would be interesting for future features related to favorite-things
rating, allowing easy and efficient traversal in order of preference.

Furthermore, some tests were introduced just to provide some examples of testing principles, the code coverage is not
complete at this point due to time constraints.

Here's an example of how to run the program with support of the Gradle application plugin:
```shell
./gradlew run --args="${PWD}/app/src/main/resources/favorite-things.txt""
```

Next Steps:
* For a more featured and modular application I would introduce PicoCLI, this framework is great for implementing
CLI applications;
* We could compile this to binary with GraalVM (PicoCLI also supports it), in this way we would get a very small and
efficient binary to run our software (even though OS specific).
* More OO design could be applied (getting the File vs Resource approaches addressed as class hierarchy on top of the
base parser class for example), extending test cases and reviewing requirements for the system.
* Introduce more features and options for parsing, some ideas would be: total ranking among Categories, least favorite
things.