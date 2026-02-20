package sillyrat.parser;

/**
 * Represents the parsed arguments for creating a todo task.
 */
public class TodoArgs {
    private final String description;

    /**
     * Constructs a new TodoArgs with the given description.
     *
     * @param description The description of the todo task.
     */
    public TodoArgs(String description) {
        this.description = description;
    }
}
