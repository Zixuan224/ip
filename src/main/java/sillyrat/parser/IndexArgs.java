package sillyrat.parser;

/**
 * Encapsulates the arguments for an index task.
 */
public class IndexArgs {
    private final int taskNumber;

    public IndexArgs(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public int getTaskNumber() {
        return taskNumber;
    }
}