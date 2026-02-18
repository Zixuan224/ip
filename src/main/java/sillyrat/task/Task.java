package sillyrat.task;

import sillyrat.common.DateTimeUtil;

/**
 * Represents a task with description and no time attributes.
 * This class serves as a base class for other task types.
 */
public class Task {
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
        String[] parts = line.split("\t");

        String type = parts[0];
        boolean done = parts[1].equals("1");

        Task task;
        switch (type) {
        case "T":
            task = new Todo(parts[2]);
            break;
        case "D":
            task = new Deadline(parts[2], DateTimeUtil.parseStorageDateTime(parts[3]));
            break;
        case "E":
            task = new Event(parts[2],
                    DateTimeUtil.parseStorageDateTime(parts[3]),
                    DateTimeUtil.parseStorageDateTime(parts[4]));
            break;
        default:
            task = new Task(parts.length > 2 ? parts[2] : "");
            break;
        }

        if (done) {
            task.markDone();
        }

        return task;
    }

    public String getDescription() {
        return description;
    }

    public String toSaveString() {
        return getTypeIcon() + "\t" + (isDone ? "1" : "0") + "\t" + description;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
