package sillyrat.parser;

/**
 * Represents arguments for creating a todo task.
 */
public class TodoArgs {
    private final String description;

    public TodoArgs(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
