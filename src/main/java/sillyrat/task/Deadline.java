package sillyrat.task;
import java.time.LocalDateTime;

import sillyrat.common.DateTimeUtil;

/**
 * Represents a task that must be completed by a specific date and time.
 * This class encapsulates a deadline attribute and provides methods for specialized display and storage formatting.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Initializes a new Deadline task with a description and time limit.
     *
     * @param description The text describing the task.
     * @param by The date and time by which the task must be finished.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toSaveString() {
        return "D" + FIELD_SEPARATOR + (isDone ? DONE_MARKER : NOT_DONE_MARKER)
                + FIELD_SEPARATOR + description
                + FIELD_SEPARATOR + DateTimeUtil.toStorageString(by);
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + " (by: " + DateTimeUtil.toDisplayString(by) + ")";
    }
}
