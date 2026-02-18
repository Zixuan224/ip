package sillyrat.task;

/**
 * Represents the basic task without deadline or timeframe
 * This class provides a unique identifier 'T' for task categorisation and display purposes
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
