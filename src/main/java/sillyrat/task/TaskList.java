package sillyrat.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    public void add(Task task) {
        assert task != null : "Cannot add null task to list";
        tasks.add(task);
    }

    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Index must be within valid range";
        return tasks.remove(index);
    }

    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Index must be within valid range";
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the list of tasks.
     * @return The list of tasks.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Finds tasks containing the given keywords in their descriptions.
     * @param keywords The keywords to search for.
     * @return A list of tasks matching the keywords.
     */
    public List<Task> find(String keywords) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keywords))
                .toList();
    }

    /**
     * Returns undone deadlines and events that are due/starting within the given number of days.
     *
     * @param days The number of days ahead to look for upcoming tasks.
     * @return A list of upcoming tasks sorted naturally by list order.
     */
    public List<Task> getUpcoming(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusDays(days);

        return tasks.stream()
                .filter(task -> !task.isDone())
                .filter(task -> isUpcoming(task, now, cutoff))
                .toList();
    }

    private static boolean isUpcoming(Task task, LocalDateTime now, LocalDateTime cutoff) {
        if (task instanceof Deadline) {
            LocalDateTime by = ((Deadline) task).getBy();
            return !by.isBefore(now) && !by.isAfter(cutoff);
        }
        if (task instanceof Event) {
            LocalDateTime from = ((Event) task).getFrom();
            return !from.isBefore(now) && !from.isAfter(cutoff);
        }
        return false;
    }
}
