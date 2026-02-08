import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SillyRat {

    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        List<String> lines = Storage.loadData();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                tasks.add(Task.toLoadTask(line));
            } catch (Exception e) {
                System.out.println(" Skipped corrupted line: " + line);
            }
        }

        printLine();
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
        printLine();

        while (true) {
            String input = in.nextLine().trim();

            if (input.equals("bye")) {
                printLine();
                System.out.println(" See you! Please bring more food next time :)");
                printLine();
                break;
            }

            try {
                handleCommand(input, tasks);
            } catch (SillyRatException e) {
                printLine();
                System.out.println(" " + e.getMessage());
                printLine();
            } catch (Exception e) {
                printLine();
                System.out.println(" Oops... I tripped over my own tail. Shall we try again?");
                printLine();
            }
        }

        in.close();
    }

    private static void handleCommand(String input, ArrayList<Task> tasks) throws SillyRatException, IOException {
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
                saveToFile(tasks);
                break;

            case "deadline":
                doDeadline(rest, tasks);
                saveToFile(tasks);
                break;

            case "event":
                doEvent(rest, tasks);
                saveToFile(tasks);
                break;

            case "mark": {
                doMark(rest, tasks, true);
                saveToFile(tasks);
                break;
            }

            case "unmark": {
                doMark(rest, tasks, false);
                saveToFile(tasks);
                break;
            }

            case "delete": {
                doDelete(rest, tasks);
                saveToFile(tasks);
                break;
            }

            default:
                throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: todo, deadline, event, list, mark, unmark, delete, bye");
        }
    }

    // ---------------- Commands ----------------

    private static void doList(ArrayList<Task> tasks) {
        printLine();
        if (tasks.isEmpty()) {
            System.out.println(" Your list is empty. Feed me tasks with todo/deadline/event.");
            printLine();
            return;
        }

        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        printLine();
    }

    private static void doTodo(String rest, ArrayList<Task> tasks) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Todo needs a description. Example: todo borrow cheese");
        }

        Task t = new Todo(rest);
        tasks.add(t);

        printLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void doDeadline(String rest, ArrayList<Task> tasks) throws SillyRatException {
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
            throw new SillyRatException("Deadline description cannot be empty. Example: deadline return book /by Sunday");
        }
        if (if (byRaw.isEmpty()) {
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

        printLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void doEvent(String rest, ArrayList<Task> tasks) throws SillyRatException {
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
        if (from.isEmpty()) {
            throw new SillyRatException("Event start time cannot be empty.");
        }
        if (to.isEmpty()) {
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

        printLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void doMark(String rest, ArrayList<Task> tasks, boolean markDone) throws SillyRatException {
        int idx = parseIndex(rest, tasks.size());

        Task t = tasks.get(idx);
        if (markDone) {
            if (t.isDone) {
                printLine();
                System.out.println(" Master... It's already marked done.");
                printLine();
            } else {
                t.markDone();
                printLine();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + t);
                printLine();
            }
        } else {
            if (t.isDone) {
                t.unmarkDone();
                printLine();
                System.out.println(" OK! I've marked this task as not done yet:");
                System.out.println("   " + t);
                printLine();
            } else {
                printLine();
                System.out.println(" Wake up Master... It's unchecked already.");
                printLine();
            }
        }
    }

    private static void doDelete(String rest, ArrayList<Task> tasks) throws SillyRatException {
        int idx = parseIndex(rest, tasks.size());
        Task removed = tasks.remove(idx);

        printLine();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    // ---------------- Helpers ----------------

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

        return n - 1; // convert to 0-based
    }

    private static void saveToFile(ArrayList<Task> tasks) throws IOException {
        List<String> lines = tasks.stream()
                .map(Task::toSaveString)
                .toList();
        Storage.saveData(lines);
    }

    private static void printLine() {
        System.out.println(LINE);
    }
}

/* ===================== Storage Class ===================== */
class Storage {
    private static final Path FILE_PATH = Paths.get("data", "silly-rat.txt");

    private static void ensureExists() throws IOException {
        Path dir = FILE_PATH.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        if (Files.notExists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }
    }

    public static List<String> loadData() throws IOException {
        ensureExists();
        return Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);
    }

    public static void saveData(List<String> lines) throws IOException {
        ensureExists();
        Files.write(FILE_PATH, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

/* ===================== Task Classes (Inheritance) ===================== */

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
        return " "; // overridden by subclasses
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

        if (done) { task.markDone(); }

        return task;
    }

    public String toSaveString() {
        return getTypeIcon() + "\t" + (isDone? "1" : "0") + "\t" + description;
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
    private LocalDateTime by;

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
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description + " (by: " + by + ")";
    }
}

class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

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

/* ===================== Custom Exception ===================== */

class SillyRatException extends Exception {
    public SillyRatException(String message) {
        super(message);
    }
}

/* ===================== DateTimeUtil Class ===================== */

class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm");

    private static final DateTimeFormatter STORAGE =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter[] USER_DATE_TIME_FORMATS = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),     // 2/12/2019 1800
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"),  // 2019-10-15 18:00
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")    // 2019-10-15 1800
    };

    private static final DateTimeFormatter[] USER_DATE_ONLY_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd")         // 2019-10-15
    };

    private DateTimeUtil() {}

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
