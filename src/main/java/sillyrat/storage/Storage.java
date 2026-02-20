package sillyrat.storage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import sillyrat.task.Task;
import sillyrat.task.TaskList;

/**
 * Handles reading and writing tasks to a file on disk.
 * Provides methods to load tasks from persistent storage and save them back.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a new Storage instance with the specified file path.
     *
     * @param filePath The path to the storage file.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path cannot be null or empty";
        this.filePath = Paths.get(filePath);
        assert this.filePath != null : "Path creation should succeed";
    }

    private void ensureExists() throws IOException {
        Path dir = this.filePath.getParent();

        boolean hasParentDir = dir != null;
        if (hasParentDir && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        if (Files.notExists(this.filePath)) {
            Files.createFile(this.filePath);
        }
    }

    /**
     * Loads tasks from the storage file and returns them as a list.
     * Skips empty lines in the file. Creates the file if it does not yet exist.
     *
     * @return A list of tasks loaded from the file.
     * @throws IOException If an I/O error occurs while reading.
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
     * Saves all tasks in the given task list to the storage file.
     * Overwrites the existing file content.
     *
     * @param tasks The task list to be saved.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void save(TaskList tasks) throws IOException {
        ensureExists();
        List<String> lines = tasks.asList().stream()
                .map(Task::toSaveString)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
