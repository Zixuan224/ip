package sillyrat.parser;

/**
 * Encapsulates the arguments for a command that operates on a task by its index number.
 */
public class IndexArgs {
    private final int taskNumber;

    /**
     * Constructs a new IndexArgs with the given one-based task number.
     *
     * @param taskNumber The one-based index of the target task.
     */
    public IndexArgs(int taskNumber) {
        this.taskNumber = taskNumber;
    }
}
