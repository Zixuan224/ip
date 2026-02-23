package sillyrat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import sillyrat.common.DateTimeUtil;
import sillyrat.common.SillyRatException;
import sillyrat.parser.Command;
import sillyrat.parser.DeadlineArgs;
import sillyrat.parser.EventArgs;
import sillyrat.parser.FindArgs;
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

/**
 * Represents the main application logic for the SillyRat chatbot.
 * Coordinates parsing, task management, storage, and response generation.
 * Javadoc comments in this class were written with the assistance of AI (ChatGPT, Claude).
 */
public class SillyRat {

    private static final Parser PARSER = new Parser();
    /** Number of days ahead to check for upcoming task reminders. */
    private static final int REMINDER_DAYS = 7;

    private final Storage storage;
    private final TaskList tasks;

    /**
     * Initializes the SillyRat application with a specified storage file path.
     * Loads existing tasks from the file, or starts with an empty list if loading fails.
     *
     * @param filePath The path to the file used for persistent task storage.
     */
    public SillyRat(String filePath) {
        this.storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (Exception e) {
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /**
     * Initializes the SillyRat application with the default storage file path.
     */
    public SillyRat() {
        this("data/silly-rat.txt");
    }

    /**
     * Returns a startup greeting with reminders about upcoming tasks within
     * the next {@value #REMINDER_DAYS} days.
     * This method is intended to be called once when the application first launches.
     *
     * @return The greeting and reminder message.
     */
    public String getStartupReminder() {
        String greeting = "Hello Master! Silly Rat at your service!";
        String guide = "\n\nHere's what I can do:"
                + "\n• New task: todo, deadline, event"
                + "\n• Manage: list, mark, unmark, delete"
                + "\n• Search: find"
                + "\n• Reminders: remind"
                + "\n• Exit: bye";

        List<Task> upcoming = tasks.getUpcoming(REMINDER_DAYS);

        if (upcoming.isEmpty()) {
            return greeting + guide + "\n\nNo upcoming deadlines or events. Chill ya.";
        }

        StringBuilder sb = new StringBuilder(greeting);
        sb.append(guide);
        sb.append("\n\nSqueak! Reminder — these tasks are due in the next ")
                .append(REMINDER_DAYS).append(" days:\n");
        for (int i = 0; i < upcoming.size(); i++) {
            sb.append(i + 1).append(". ").append(upcoming.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Processes user input and returns the chatbot's response string.
     * Parses the input into a command, executes it, and returns an appropriate reply.
     * If the input is invalid, returns a user-friendly error message.
     *
     * @param input The raw user input string.
     * @return The response message to display to the user.
     */
    public String getResponse(String input) {
        try {
            ParsedCommand parsed = PARSER.parse(input);
            assert parsed != null : "Parser should never return null";
            assert parsed.getCommand() != null : "Command should never be null";
            return executeCommand(parsed);
        } catch (SillyRatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Oops... I tripped over my own tail. Shall we try again?";
        }
    }

    /**
     * Dispatches the parsed command to the appropriate handler method.
     *
     * @param parsed The parsed command containing the command type and arguments.
     * @return The response message for the executed command.
     * @throws SillyRatException If the command arguments are invalid.
     * @throws IOException If an error occurs while saving tasks.
     */
    private String executeCommand(ParsedCommand parsed) throws SillyRatException, IOException {
        Command command = parsed.getCommand();

        switch (command) {
        case LIST:
            return replyList();

        case BYE:
            return "See you! Please bring more food next time :)";

        case TODO:
            return addTaskAndSave(replyTodo((TodoArgs) parsed.getArgs()));

        case DEADLINE:
            return addTaskAndSave(replyDeadline((DeadlineArgs) parsed.getArgs()));

        case EVENT:
            return addTaskAndSave(replyEvent((EventArgs) parsed.getArgs()));

        case MARK:
            return modifyTaskAndSave(parsed, true);

        case UNMARK:
            return modifyTaskAndSave(parsed, false);

        case DELETE:
            return deleteTaskAndSave(parsed);

        case FIND:
            return replyFind((FindArgs) parsed.getArgs());

        case REMIND:
            return replyRemind();

        default:
            throw new SillyRatException("I don't understand Meowese, Master. "
                    + "\n\nTalk in Squeakese:"
                    + "\n• New task: todo, deadline, event"
                    + "\n• Manage: list, mark, unmark, delete"
                    + "\n• Search: find"
                    + "\n• Reminders: remind");
        }
    }

    /**
     * Saves the current task list to storage and returns the given reply message.
     *
     * @param replyMessage The response message to return after saving.
     * @return The same reply message passed in.
     * @throws IOException If an error occurs while saving tasks.
     */
    private String addTaskAndSave(String replyMessage) throws IOException {
        storage.save(tasks);
        return replyMessage;
    }

    /**
     * Marks or unmarks a task identified by the parsed command, saves, and returns a reply.
     *
     * @param parsed The parsed command containing the task index.
     * @param markDone True to mark the task as done, false to unmark it.
     * @return The response message indicating the result.
     * @throws SillyRatException If the task index is out of range.
     * @throws IOException If an error occurs while saving tasks.
     */
    private String modifyTaskAndSave(ParsedCommand parsed, boolean markDone)
            throws SillyRatException, IOException {
        IndexArgs args = (IndexArgs) parsed.getArgs();
        int idx = toValidIndex(args.getTaskNumber(), tasks.size());
        String msg = replyMark(idx, markDone);
        storage.save(tasks);
        return msg;
    }

    /**
     * Deletes a task identified by the parsed command, saves, and returns a reply.
     *
     * @param parsed The parsed command containing the task index.
     * @return The response message indicating the result.
     * @throws SillyRatException If the task index is out of range.
     * @throws IOException If an error occurs while saving tasks.
     */
    private String deleteTaskAndSave(ParsedCommand parsed) throws SillyRatException, IOException {
        IndexArgs args = (IndexArgs) parsed.getArgs();
        int idx = toValidIndex(args.getTaskNumber(), tasks.size());
        String msg = replyDelete(idx);
        storage.save(tasks);
        return msg;
    }

    //region Responses

    /**
     * Returns a formatted listing of all tasks currently in the task list.
     *
     * @return The formatted task list string, or a message if the list is empty.
     */
    private String replyList() {
        if (tasks.isEmpty()) {
            return "Nothing on the list now! Feed me tasks with todo/deadline/event.";
        }
        StringBuilder sb = new StringBuilder("Here are your master plans:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Adds a new todo task and returns a confirmation message.
     *
     * @param args The parsed todo arguments containing the task description.
     * @return The confirmation message with the added task details.
     */
    private String replyTodo(TodoArgs args) {
        Task task = new Todo(args.getDescription());
        tasks.add(task);
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Adds a new deadline task and returns a confirmation message.
     *
     * @param args The parsed deadline arguments containing description and due date.
     * @return The confirmation message with the added task details.
     * @throws SillyRatException If the date/time format is invalid.
     */
    private String replyDeadline(DeadlineArgs args) throws SillyRatException {
        LocalDateTime by;
        try {
            by = DateTimeUtil.parseUserDateTime(args.getByRaw());
        } catch (IllegalArgumentException e) {
            throw new SillyRatException(e.getMessage());
        }

        Task task = new Deadline(args.getDescription(), by);
        tasks.add(task);
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Adds a new event task and returns a confirmation message.
     *
     * @param args The parsed event arguments containing description, start, and end times.
     * @return The confirmation message with the added task details.
     * @throws SillyRatException If the date/time format is invalid or end is before start.
     */
    private String replyEvent(EventArgs args) throws SillyRatException {
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
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Marks or unmarks a task as done and returns a status message.
     *
     * @param idx The zero-based index of the task in the list.
     * @param markDone True to mark as done, false to unmark.
     * @return The response message indicating the updated task status.
     */
    private String replyMark(int idx, boolean markDone) {
        Task task = tasks.get(idx);

        if (markDone && task.isDone()) {
            return "Master... It's already marked done.";
        }
        if (!markDone && !task.isDone()) {
            return "Wake up Master... It's unchecked already.";
        }

        if (markDone) {
            task.markDone();
            return "Nice! I've marked this task as done:\n  " + task;
        }

        task.unmarkDone();
        return "OK! I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Removes a task from the list and returns a confirmation message.
     *
     * @param idx The zero-based index of the task to remove.
     * @return The confirmation message with the removed task details.
     */
    private String replyDelete(int idx) {
        Task removed = tasks.remove(idx);
        return "Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Searches for tasks matching the given keywords and returns the results.
     *
     * @param args The parsed find arguments containing the search string.
     * @return The formatted list of matching tasks, or a message if none are found.
     */
    private String replyFind(FindArgs args) {
        String keywords = args.getSearchString();
        List<Task> found = tasks.find(keywords);

        if (found.isEmpty()) {
            return "No tasks found matching your search term, Master.";
        }

        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < found.size(); i++) {
            sb.append(i + 1).append(". ").append(found.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Returns a reminder of undone deadlines and events due within the next
     * {@value #REMINDER_DAYS} days.
     *
     * @return The formatted reminder string, or a message if there are no upcoming tasks.
     */
    private String replyRemind() {
        List<Task> upcoming = tasks.getUpcoming(REMINDER_DAYS);

        if (upcoming.isEmpty()) {
            return "No upcoming deadlines or events in the next "
                    + REMINDER_DAYS + " days. Relax, Master!";
        }

        StringBuilder sb = new StringBuilder("Squeak! These tasks are due in the next "
                + REMINDER_DAYS + " days:\n");
        for (int i = 0; i < upcoming.size(); i++) {
            sb.append(i + 1).append(". ").append(upcoming.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Converts a one-based task number to a zero-based index after validation.
     *
     * @param taskNumber The one-based task number from user input.
     * @param size The current number of tasks in the list.
     * @return The zero-based index.
     * @throws SillyRatException If the list is empty or the task number is out of range.
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
    //endregion
}
