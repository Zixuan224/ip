package sillyrat.parser;

/**
 * Represents arguments for an event task.
 */
public class EventArgs {
    private final String description;
    private final String fromRaw;
    private final String toRaw;

    /**
     * Initializes a new instance of the EventArgs class.
     *
     * @param description The description of the event task.
     * @param fromRaw     The raw 'from' date/time string.
     * @param toRaw       The raw 'to' date/time string.
     */
    public EventArgs(String description, String fromRaw, String toRaw) {
        this.description = description;
        this.fromRaw = fromRaw;
        this.toRaw = toRaw;
    }

    public String getDescription() {
        return description;
    }

    public String getFromRaw() {
        return fromRaw;
    }

    public String getToRaw() {
        return toRaw;
    }
}
