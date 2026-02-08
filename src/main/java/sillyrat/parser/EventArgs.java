package sillyrat.parser;

public class EventArgs {
    private final String description;
    private final String fromRaw;
    private final String toRaw;
    public EventArgs(String description, String fromRaw, String toRaw) {
        this.description = description;
        this.fromRaw = fromRaw;
        this.toRaw = toRaw;
    }
    public String getDescription() { return description; }
    public String getFromRaw() { return fromRaw; }
    public String getToRaw() { return toRaw; }
}