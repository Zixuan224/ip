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
    private static final Parser parser = new Parser();

    public static void main(String[] args) throws IOException {
        ui = new Ui();
        Storage storage = new Storage("data/silly-rat.txt");

        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            tasks = new TaskList();
        }

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

        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            try {
                isExit = handleCommand(input, tasks, storage);
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

    private static boolean handleCommand(String input, TaskList tasks, Storage storage)
            throws SillyRatException, IOException {

        ParsedCommand parsed = parser.parse(input);
        String command = parsed.getCommandWord();

        switch (command) {
            case "list":
                doList(tasks);
                return false;

            case "bye":
                ui.showLine();
                System.out.println(" See you! Please bring more food next time :)");
                ui.showLine();
                return true;

            case "todo": {
                TodoArgs args = (TodoArgs) parsed.getArgs();
                doTodo(args, tasks);
                storage.save(tasks);
                return false;
            }

            case "deadline": {
                DeadlineArgs args = (DeadlineArgs) parsed.getArgs();
                doDeadline(args, tasks);
                storage.save(tasks);
                return false;
            }

            case "event": {
                EventArgs args = (EventArgs) parsed.getArgs();
                doEvent(args, tasks);
                storage.save(tasks);
                return false;
            }

            case "mark": {
                IndexArgs args = (IndexArgs) parsed.getArgs();
                int idx = toValidIndex(args.getTaskNumber(), tasks.size());
                doMark(idx, tasks, true);
                storage.save(tasks);
                return false;
            }

            case "unmark": {
                IndexArgs args = (IndexArgs) parsed.getArgs();
                int idx = toValidIndex(args.getTaskNumber(), tasks.size());
                doMark(idx, tasks, false);
                storage.save(tasks);
                return false;
            }

            case "delete": {
                IndexArgs args = (IndexArgs) parsed.getArgs();
                int idx = toValidIndex(args.getTaskNumber(), tasks.size());
                doDelete(idx, tasks);
                storage.save(tasks);
                return false;
            }

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

    private static void doTodo(TodoArgs args, TaskList tasks) {
        Task t = new Todo(args.getDescription());
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doDeadline(DeadlineArgs args, TaskList tasks) throws SillyRatException {
        LocalDateTime by;
        try {
            by = DateTimeUtil.parseUserDateTime(args.getByRaw());
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }

        Task t = new Deadline(args.getDescription(), by);
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doEvent(EventArgs args, TaskList tasks) throws SillyRatException {
        LocalDateTime from;
        LocalDateTime to;
        try {
            from = DateTimeUtil.parseUserDateTime(args.getFromRaw());
            to = DateTimeUtil.parseUserDateTime(args.getToRaw());
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }

        if (to.isBefore(from)) {
            throw new SillyRatException("Event end must not be earlier than start.");
        }

        Task t = new Event(args.getDescription(), from, to);
        tasks.add(t);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doMark(int idx, TaskList tasks, boolean markDone) {
        Task t = tasks.get(idx);

        if (markDone) {
            if (t.isDone) {
                ui.showLine();
                System.out.println(" Master... It's already marked done.");
                ui.showLine();
                return;
            }
            t.markDone();
            ui.showLine();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + t);
            ui.showLine();
            return;
        }

        if (!t.isDone) {
            ui.showLine();
            System.out.println(" Wake up Master... It's unchecked already.");
            ui.showLine();
            return;
        }

        t.unmarkDone();
        ui.showLine();
        System.out.println(" OK! I've marked this task as not done yet:");
        System.out.println("   " + t);
        ui.showLine();
    }

    private static void doDelete(int idx, TaskList tasks) {
        Task removed = tasks.remove(idx);

        ui.showLine();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static int toValidIndex(int taskNumber, int size) throws SillyRatException {
        if (size == 0) {
            throw new SillyRatException("Your list is empty. Nothing to do here.");
        }
        if (taskNumber < 1 || taskNumber > size) {
            throw new SillyRatException("Task number out of range. Use 1 to " + size + ".");
        }
        return taskNumber - 1;
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
            tasks.add(Task.toLoadTask(line));
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
                break;
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

class ParsedCommand {
    private final String commandWord;
    private final Object args;

    public ParsedCommand(String commandWord, Object args) {
        this.commandWord = commandWord;
        this.args = args;
    }

    public String getCommandWord() {
        return commandWord;
    }

    public Object getArgs() {
        return args;
    }
}

class Parser {

    public ParsedCommand parse(String input) throws SillyRatException {
        if (input == null || input.trim().isEmpty()) {
            throw new SillyRatException("Say something, Master. My tiny ears heard nothing.");
        }

        String trimmed = input.trim();
        String[] parts = trimmed.split(" ", 2);
        String commandWord = parts[0];
        String rest = (parts.length > 1) ? parts[1].trim() : "";

        switch (commandWord) {
            case "list":
                requireNoArgs(commandWord, rest);
                return new ParsedCommand(commandWord, new NoArgs());

            case "bye":
                requireNoArgs(commandWord, rest);
                return new ParsedCommand(commandWord, new NoArgs());

            case "todo":
                return new ParsedCommand(commandWord, parseTodoArgs(rest));

            case "deadline":
                return new ParsedCommand(commandWord, parseDeadlineArgs(rest));

            case "event":
                return new ParsedCommand(commandWord, parseEventArgs(rest));

            case "mark":
            case "unmark":
            case "delete":
                return new ParsedCommand(commandWord, parseIndexArgs(commandWord, rest));

            default:
                throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: "
                        + "todo, deadline, event, list, mark, unmark, delete, bye");
        }
    }

    private void requireNoArgs(String commandWord, String rest) throws SillyRatException {
        if (rest != null && !rest.isBlank()) {
            throw new SillyRatException(commandWord + " does not take any extra words.");
        }
    }

    private TodoArgs parseTodoArgs(String rest) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Todo needs a description. Example: todo borrow cheese");
        }
        return new TodoArgs(rest);
    }

    private DeadlineArgs parseDeadlineArgs(String rest) throws SillyRatException {
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

        return new DeadlineArgs(desc, byRaw);
    }

    private EventArgs parseEventArgs(String rest) throws SillyRatException {
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

        return new EventArgs(desc, fromRaw, toRaw);
    }

    private IndexArgs parseIndexArgs(String commandWord, String rest) throws SillyRatException {
        if (rest == null || rest.trim().isEmpty()) {
            throw new SillyRatException("Please provide a task number. Example: " + commandWord + " 2");
        }

        int n;
        try {
            n = Integer.parseInt(rest.trim());
        } catch (NumberFormatException e) {
            throw new SillyRatException("Task number must be a number. Example: " + commandWord + " 3");
        }

        if (n < 1) {
            throw new SillyRatException("Task number must be at least 1.");
        }

        return new IndexArgs(n);
    }
}

class NoArgs {
}

class TodoArgs {
    private final String description;

    public TodoArgs(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

class DeadlineArgs {
    private final String description;
    private final String byRaw;

    public DeadlineArgs(String description, String byRaw) {
        this.description = description;
        this.byRaw = byRaw;
    }

    public String getDescription() {
        return description;
    }

    public String getByRaw() {
        return byRaw;
    }
}

class EventArgs {
    private final String description;
    private final String fromRaw;
    private final String toRaw;

    public EventArgs(String description, String fromRaw, String toRaw) {
        this.description = description;
        this.fromRaw = fromRaw;
        this.toRaw = toRaw;
    }

    public String getDescription() {
        return description;
    }

    public String getFromRaw() {
        return fromRaw;
    }

    public String getToRaw() {
        return toRaw;
    }
}

class IndexArgs {
    private final int taskNumber;

    public IndexArgs(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public int getTaskNumber() {
        return taskNumber;
    }
}
