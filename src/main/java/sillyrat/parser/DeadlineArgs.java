package sillyrat.parser;

public class DeadlineArgs {
    private final String description;
    private final String byRaw;

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