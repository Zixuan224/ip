package sillyrat.parser;

/**
 * Represents arguments for a deadline task.
 */
public class DeadlineArgs {
    private final String description;
    private final String byRaw;

    /**
     * Initializes a new instance of the DeadlineArgs class.
     *
     * @param description The description of the deadline task.
     * @param byRaw       The raw 'by' date/time string.
     */
    public DeadlineArgs(String description, String byRaw) {
        this.description = description;
        this.byRaw = byRaw;
    }

    public String getDescription() {
        return description;
    }

    public String getByRaw() {
        return byRaw;
    }
}
