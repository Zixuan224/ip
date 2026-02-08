import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SillyRat {

    private static Ui ui;

    public static void main(String[] args) throws IOException {
        ui = new Ui();
        Storage storage = new Storage("data/silly-rat.txt");
        TaskList tasks = new TaskList(storage.load());

        ui.showLine();
        System.out.println(" Hello, Master! I'm Little Silly Rat 🐀");
        if (tasks.isEmpty()) {
            System.out.println(" You are so free, Master. Nothing to do eh.");
        } else {
            System.out.println(" Here's your tasks.");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
        System.out.println(" What are you up to now?");
        ui.showLine();

        while (true) {
            String input = ui.readCommand();

            if (input.equals("bye")) {
                ui.showLine();
                System.out.println(" See you! Please bring more food next time :)");
                ui.showLine();
                break;
            }

            try {
                handleCommand(input, tasks, storage);
            } catch (SillyRatException e) {
                ui.showLine();
                System.out.println(" " + e.getMessage());
                ui.showLine();
            } catch (Exception e) {
                ui.showLine();
                System.out.println(" Oops... I tripped over my own tail. Shall we try again?");
                ui.showLine();
            }
        }

        ui.close();
    }

    private static void handleCommand(String input, TaskList tasks, Storage storage)
            throws SillyRatException, IOException {
        if (input.isEmpty()) {
            throw new SillyRatException("Say something, Master. My tiny ears heard nothing.");
        }

        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String rest = (parts.length > 1) ? parts[1].trim() : "";

        switch (command) {
            case "list":
                doList(tasks);
                break;

            case "todo":
                doTodo(rest, tasks);
                storage.save(tasks);
                break;

            case "deadline":
                doDeadline(rest, tasks);
                storage.save(tasks);
                break;

            case "event":
                doEvent(rest, tasks);
                storage.save(tasks);
                break;

            case "mark":
                doMark(rest, tasks, true);
                storage.save(tasks);
                break;

            case "unmark":
                doMark(rest, tasks, false);
                storage.save(tasks);
                break;

            case "delete":
                doDelete(rest, tasks);
                storage.save(tasks);
                break;

            default:
                throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: "
                        + "todo, deadline, event, list, mark, unmark, delete, bye");
        }
    }

    private static void doList(TaskList tasks) {
        ui.showLine();
        if (tasks.isEmpty()) {
            System.out.println(" Your list is empty. Feed me tasks with todo/deadline/event.");
            ui.showLine();
            return;
        }

        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        ui.showLine();
    }

    private static void doTodo(String rest, TaskList tasks) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Todo needs a description. Example: todo borrow cheese");
        }

        Task t = new Todo(rest);
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doDeadline(String rest, TaskList tasks) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Deadline needs details. Example: deadline submit report /by Sunday");
        }

        String[] split = rest.split(" /by ", 2);
        if (split.length < 2) {
            throw new SillyRatException("Deadline format: deadline <task> /by <when>");
        }

        String desc = split[0].trim();
        String byRaw = split[1].trim();

        if (desc.isEmpty()) {
            throw new SillyRatException(
                    "Deadline description cannot be empty. Example: deadline return book /by Sunday");
        }
        if (byRaw.isEmpty()) {
            throw new SillyRatException("Deadline time cannot be empty. Example: deadline return book /by Sunday");
        }

        LocalDateTime by;
        try {
            by = DateTimeUtil.parseUserDateTime(byRaw);
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }

        Task t = new Deadline(desc, by);
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doEvent(String rest, TaskList tasks) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Event needs details. Example: event meeting /from Mon 2pm /to 4pm");
        }

        String[] fromSplit = rest.split(" /from ", 2);
        if (fromSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from <start> /to <end>");
        }

        String desc = fromSplit[0].trim();
        String afterFrom = fromSplit[1];

        String[] toSplit = afterFrom.split(" /to ", 2);
        if (toSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from <start> /to <end>");
        }

        String fromRaw = toSplit[0].trim();
        String toRaw = toSplit[1].trim();

        if (desc.isEmpty()) {
            throw new SillyRatException("Event description cannot be empty.");
        }
        if (fromRaw.isEmpty()) {
            throw new SillyRatException("Event start time cannot be empty.");
        }
        if (toRaw.isEmpty()) {
            throw new SillyRatException("Event end time cannot be empty.");
        }

        LocalDateTime from;
        LocalDateTime to;
        try {
            from = DateTimeUtil.parseUserDateTime(fromRaw);
            to = DateTimeUtil.parseUserDateTime(toRaw);
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }
        if (to.isBefore(from)) {
            throw new SillyRatException("Event end must not be earlier than start.");
        }

        Task t = new Event(desc, from, to);
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doMark(String rest, TaskList tasks, boolean markDone) throws SillyRatException {
        int idx = parseIndex(rest, tasks.size());

        Task t = tasks.get(idx);
        if (markDone) {
            if (t.isDone) {
                ui.showLine();
                System.out.println(" Master... It's already marked done.");
                ui.showLine();
            } else {
                t.markDone();
                ui.showLine();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + t);
                ui.showLine();
            }
        } else {
            if (t.isDone) {
                t.unmarkDone();
                ui.showLine();
                System.out.println(" OK! I've marked this task as not done yet:");
                System.out.println("   " + t);
                ui.showLine();
            } else {
                ui.showLine();
                System.out.println(" Wake up Master... It's unchecked already.");
                ui.showLine();
            }
        }
    }

    private static void doDelete(String rest, TaskList tasks) throws SillyRatException {
        int idx = parseIndex(rest, tasks.size());
        Task removed = tasks.remove(idx);

        ui.showLine();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static int parseIndex(String rest, int size) throws SillyRatException {
        if (rest == null || rest.trim().isEmpty()) {
            throw new SillyRatException("Please provide a task number. Example: mark 2");
        }
        if (size == 0) {
            throw new SillyRatException("Your list is empty. Nothing to do here.");
        }

        int n;
        try {
            n = Integer.parseInt(rest.trim());
        } catch (NumberFormatException e) {
            throw new SillyRatException("Task number must be a number. Example: delete 3");
        }

        if (n < 1 || n > size) {
            throw new SillyRatException("Task number out of range. Use 1 to " + size + ".");
        }

        return n - 1;
    }
}

class Storage {
    private final Path filePath;

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

    public List<Task> load() throws IOException {
        ensureExists();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                tasks.add(Task.toLoadTask(line));
            } catch (Exception e) {
                System.out.println(" Skipped corrupted line: " + line);
            }
        }

        return tasks;
    }

    public void save(TaskList tasks) throws IOException {
        ensureExists();
        List<String> lines = tasks.asList().stream()
                .map(Task::toSaveString)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

class Task {
    protected String description;
    protected boolean isDone;

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

    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    protected String getTypeIcon() {
        return " ";
    }

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
        }

        if (done) {
            task.markDone();
        }

        return task;
    }

    public String toSaveString() {
        return getTypeIcon() + "\t" + (isDone ? "1" : "0") + "\t" + description;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}

class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}

class Deadline extends Task {
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

class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

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

class SillyRatException extends Exception {
    public SillyRatException(String message) {
        super(message);
    }
}

class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm");

    private static final DateTimeFormatter STORAGE =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter[] USER_DATE_TIME_FORMATS = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
    };

    private static final DateTimeFormatter[] USER_DATE_ONLY_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
    };

    private DateTimeUtil() {
    }

    public static LocalDateTime parseUserDateTime(String raw) {
        String s = raw.trim();

        for (DateTimeFormatter f : USER_DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }

        for (DateTimeFormatter f : USER_DATE_ONLY_FORMATS) {
            try {
                LocalDate d = LocalDate.parse(s, f);
                return d.atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }

        throw new IllegalArgumentException(
                "Invalid date/time format.\n"
                        + "Accepted formats:\n"
                        + "  - yyyy-MM-dd (e.g., 2019-10-15)\n"
                        + "  - yyyy-MM-dd HH:mm (e.g., 2019-10-15 18:00)\n"
                        + "  - d/M/yyyy HHmm (e.g., 2/12/2019 1800 means 2 Dec 2019 18:00)"
        );
    }

    public static String toDisplayString(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.toLocalDate().format(DISPLAY_DATE);
        }
        return dt.format(DISPLAY_DATE_TIME);
    }

    public static String toStorageString(LocalDateTime dt) {
        return dt.format(STORAGE);
    }

    public static LocalDateTime parseStorageDateTime(String raw) {
        return LocalDateTime.parse(raw.trim(), STORAGE);
    }
}

class Ui {
    private static final String LINE =
            "____________________________________________________________";
    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void close() {
        scanner.close();
    }
}

class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public List<Task> asList() {
        return tasks;
    }
}
