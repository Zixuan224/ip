import java.util.ArrayList;
import java.util.Scanner;

public class SillyRat {

    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        printLine();
        System.out.println(" Hello! I'm Little Silly Rat 🐀");
        System.out.println(" What trouble can I create for you today?");
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
                // Safety net (optional)
                printLine();
                System.out.println(" Oops... I tripped over my own tail. Try again?");
                printLine();
            }
        }

        in.close();
    }

    private static void handleCommand(String input, ArrayList<Task> tasks) throws SillyRatException {
        if (input.isEmpty()) {
            throw new SillyRatException("Say something, human. My tiny ears heard nothing.");
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
                break;

            case "deadline":
                doDeadline(rest, tasks);
                break;

            case "event":
                doEvent(rest, tasks);
                break;

            case "mark":
                doMark(rest, tasks, true);
                break;

            case "unmark":
                doMark(rest, tasks, false);
                break;

            case "delete":
                doDelete(rest, tasks);
                break;

            default:
                throw new SillyRatException("I don't know what that means 😵 Try: todo, deadline, event, list, mark, unmark, delete, bye");
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
        String by = split[1].trim();

        if (desc.isEmpty()) {
            throw new SillyRatException("Deadline description cannot be empty. Example: deadline return book /by Sunday");
        }
        if (by.isEmpty()) {
            throw new SillyRatException("Deadline time cannot be empty. Example: deadline return book /by Sunday");
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

        String from = toSplit[0].trim();
        String to = toSplit[1].trim();

        if (desc.isEmpty()) {
            throw new SillyRatException("Event description cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new SillyRatException("Event start time cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new SillyRatException("Event end time cannot be empty.");
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
            t.markDone();
            printLine();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + t);
            printLine();
        } else {
            t.unmarkDone();
            printLine();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + t);
            printLine();
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

    private static void printLine() {
        System.out.println(LINE);
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
    private String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description + " (by: " + by + ")";
    }
}

class Event extends Task {
    private String from;
    private String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + " (from: " + from + " to: " + to + ")";
    }
}

/* ===================== Custom Exception ===================== */

class SillyRatException extends Exception {
    public SillyRatException(String message) {
        super(message);
    }
}
