package sillyrat.parser;

/**
 * Represents arguments for a find task.
 */
public class FindArgs {
    private final String searchString;

    /**
     * Initializes a new instance of the FindArgs class.
     *
     * @param searchString The search string to find tasks.
     */
    public FindArgs(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }
}
