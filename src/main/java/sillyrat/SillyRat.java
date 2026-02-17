package sillyrat;

import sillyrat.common.DateTimeUtil;
import sillyrat.common.SillyRatException;
import sillyrat.parser.DeadlineArgs;
import sillyrat.parser.EventArgs;
import sillyrat.parser.IndexArgs;
import sillyrat.parser.ParsedCommand;
import sillyrat.parser.Parser;
import sillyrat.parser.TodoArgs;
import sillyrat.storage.Storage;
import sillyrat.task.Deadline;
import sillyrat.task.Event;
import sillyrat.task.Task;
import sillyrat.task.TaskList;
import sillyrat.task.Todo;
import sillyrat.ui.Ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Main class for the SillyRat task management application.
 * Handles user interaction and coordinates between UI, storage, and task management.
 */
public class SillyRat {

    private static Ui ui;
    private static final Parser PARSER = new Parser();

    /**
     * Main entry point for the SillyRat application.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If there are I/O errors during execution.
     */
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

    /**
     * Handles a user command and executes the corresponding action.
     *
     * @param input The user input string.
     * @param tasks The current task list.
     * @param storage The storage handler for persisting tasks.
     * @return True if the command is to exit, false otherwise.
     * @throws SillyRatException If the command is invalid or execution fails.
     * @throws IOException If there are I/O errors during storage operations.
     */
    private static boolean handleCommand(String input, TaskList tasks, Storage storage)
            throws SillyRatException, IOException {

        ParsedCommand parsed = PARSER.parse(input);
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

        case "find": {
            FindArgs args = (FindArgs) parsed.getArgs();
            doFind(args, tasks);
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
                    + "todo, deadline, event, list, mark, unmark, delete, find, bye");
        }
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks The task list to display.
     */
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

    /**
     * Creates and adds a Todo task to the task list.
     *
     * @param args The arguments containing task description.
     * @param tasks The task list to add to.
     */
    private static void doTodo(TodoArgs args, TaskList tasks) {
        Task task = new Todo(args.getDescription());
        tasks.add(task);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    /**
     * Creates and adds a Deadline task to the task list.
     *
     * @param args The arguments containing task description and deadline.
     * @param tasks The task list to add to.
     * @throws SillyRatException If the date format is invalid.
     */
    private static void doDeadline(DeadlineArgs args, TaskList tasks) throws SillyRatException {
        LocalDateTime by;
        try {
            by = DateTimeUtil.parseUserDateTime(args.getByRaw());
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }

        Task task = new Deadline(args.getDescription(), by);
        tasks.add(task);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    /**
     * Creates and adds an Event task to the task list.
     *
     * @param args The arguments containing task description, start time, and end time.
     * @param tasks The task list to add to.
     * @throws SillyRatException If the date format is invalid or end time is before start time.
     */
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

        Task task = new Event(args.getDescription(), from, to);
        tasks.add(task);

        ui.showLine();
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    /**
     * Marks or unmarks a task as done.
     *
     * @param idx The index of the task in the list.
     * @param tasks The task list.
     * @param markDone True to mark as done, false to unmark.
     */
    private static void doMark(int idx, TaskList tasks, boolean markDone) {
        Task task = tasks.get(idx);

        if (markDone) {
            if (task.isDone()) {
                ui.showLine();
                System.out.println(" Master... It's already marked done.");
                ui.showLine();
                return;
            }
            task.markDone();
            ui.showLine();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + task);
            ui.showLine();
            return;
        }

        if (!task.isDone()) {
            ui.showLine();
            System.out.println(" Wake up Master... It's unchecked already.");
            ui.showLine();
            return;
        }

        task.unmarkDone();
        ui.showLine();
        System.out.println(" OK! I've marked this task as not done yet:");
        System.out.println("   " + task);
        ui.showLine();
    }

    /**
     * Deletes a task from the task list.
     *
     * @param idx The index of the task in the list.
     * @param tasks The task list.
     */
    private static void doDelete(int idx, TaskList tasks) {
        Task removed = tasks.remove(idx);

        ui.showLine();
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
    }

    private static void doFind(FindArgs args, TaskList tasks) {
        String keywords = args.getSearchString();
        List<Task> foundTasks = tasks.find(keywords);

        ui.showLine();
        if (foundTasks.isEmpty()) {
            System.out.println(" No tasks found matching your search term, Master.");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < foundTasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." +foundTasks.get(i));
            }
        }
        ui.showLine();
    }

    /**
     * Converts the task number to a valid index for the task list.
     *
     * @param taskNumber The task number input by the user.
     * @param size The size of the task list.
     * @return The valid index for the task list.
     * @throws SillyRatException If the task number is out of range.
     */
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