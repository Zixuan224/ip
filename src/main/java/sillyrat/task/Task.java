package sillyrat.task;

import sillyrat.common.DateTimeUtil;

/**
 * Represents a task with description and no time attributes.
 * This class serves as a base class for other task types.
 */
public class Task {
    protected static final String FIELD_SEPARATOR = "\t";
    protected static final String DONE_MARKER = "1";
    protected static final String NOT_DONE_MARKER = "0";

    protected String description;
    protected boolean isDone;

    /**
     * Initializes a new Task with a description.
     * @param description The text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        isDone = true;
    }

    public void unmarkDone() {
        isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the type icon for the task.
     *
     * @return The type icon as a string.
     */
    protected String getTypeIcon() {
        return " ";
    }

    /**
     * Loads a task from the given string.
     * @param line The string representation of the task.
     * @return The loaded task object.
     */
    public static Task toLoadTask(String line) {
        String[] parts = line.split(FIELD_SEPARATOR);

        String type = parts[0];
        boolean done = parts[1].equals(DONE_MARKER);

        Task task = createTaskFromParts(type, parts);

        if (done) {
            task.markDone();
        }

        return task;
    }

    private static Task createTaskFromParts(String type, String[] parts) {
        switch (type) {
        case "T":
            return new Todo(parts[2]);
        case "D":
            return new Deadline(parts[2], DateTimeUtil.parseStorageDateTime(parts[3]));
        case "E":
            return new Event(parts[2],
                    DateTimeUtil.parseStorageDateTime(parts[3]),
                    DateTimeUtil.parseStorageDateTime(parts[4]));
        default:
            return new Task(parts.length > 2 ? parts[2] : "");
        }
    }

    public String getDescription() {
        return description;
    }

    public String toSaveString() {
        return getTypeIcon() + FIELD_SEPARATOR + (isDone ? DONE_MARKER : NOT_DONE_MARKER)
                + FIELD_SEPARATOR + description;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
