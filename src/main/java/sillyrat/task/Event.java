package sillyrat.task;
import java.time.LocalDateTime;

import sillyrat.common.DateTimeUtil;

/**
 * Represents an event task.
 * This class encapsulates an event task with a description, start time, and end time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Initialises a new Event task with a description, start time, and end time.
     * @param description The text describing the task.
     * @param from The start time of the event.
     * @param to The end time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toSaveString() {
        return "E\t" + (isDone ? "1" : "0") + "\t" + description + "\t"
                + DateTimeUtil.toStorageString(from) + "\t"
                + DateTimeUtil.toStorageString(to);
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + " (from: " + DateTimeUtil.toDisplayString(from)
                + " to: " + DateTimeUtil.toDisplayString(to) + ")";
    }
}
