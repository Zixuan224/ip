package sillyrat.task;

/**
 * Represents a basic task without a deadline or timeframe.
 * Provides a unique type identifier "T" for categorization and display purposes.
 */
public class Todo extends Task {
    /**
     * Constructs a new Todo task with the given description.
     *
     * @param description The text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
