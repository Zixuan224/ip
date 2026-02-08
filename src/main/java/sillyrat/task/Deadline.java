package sillyrat.task;

import sillyrat.common.DateTimeUtil;
import java.time.LocalDateTime;

public class Deadline extends Task {
    private final LocalDateTime by;

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
        return "D\t" + (isDone ? "1" : "0") + "\t" + description + "\t"
                + DateTimeUtil.toStorageString(by);
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + " (by: " + DateTimeUtil.toDisplayString(by) + ")";
    }
}