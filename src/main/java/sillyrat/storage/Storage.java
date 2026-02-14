package sillyrat.storage;

import sillyrat.task.Task;
import sillyrat.task.TaskList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage class to save tasks added and load tasks from the file.
 */

public class Storage {
    private final Path filePath;

    /**
     * Constructor for Storage class.
     * @param filePath Path to the storage file
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    private void ensureExists() throws IOException {
        Path dir = this.filePath.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        if (Files.notExists(this.filePath)) {
            Files.createFile(this.filePath);
        }
    }

    /**
     * Loads tasks from the storage file.
     * This method reads tasks from the file and convert them to appropriate Task objects for output.
     * @return a list of tasks loaded from the file
     * @throws IOException If an I/O error occurs
     */
    public List<Task> load() throws IOException {
        ensureExists();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            tasks.add(Task.toLoadTask(line));
        }

        return tasks;
    }

    /**
     * Saves tasks to the storage file.
     * This method converts tasks to their string representation and writes them to the file.
     * @param tasks List of tasks to be saved
     * @throws IOException If an I/O error occurs
     */
    public void save(TaskList tasks) throws IOException {
        ensureExists();
        List<String> lines = tasks.asList().stream()
                .map(Task::toSaveString)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}