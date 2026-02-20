
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

public class SillyRat {

    private static final Parser PARSER = new Parser();

    private final Storage storage;
    private final TaskList tasks;

    public SillyRat(String filePath) {
        this.storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    public SillyRat() {
        this("data/silly-rat.txt");
    }

    public String getResponse(String input) {
        try {
            ParsedCommand parsed = PARSER.parse(input);
            return executeCommand(parsed);
        } catch (SillyRatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Oops... I tripped over my own tail. Shall we try again?";
        }
    }

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

        default:
            throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: "
                    + "todo, deadline, event, list, mark, unmark, delete, find, bye");
        }
    }

    private String addTaskAndSave(String replyMessage) throws IOException {
        storage.save(tasks);
        return replyMessage;
    }

    private String modifyTaskAndSave(ParsedCommand parsed, boolean markDone)
            throws SillyRatException, IOException {
        IndexArgs args = (IndexArgs) parsed.getArgs();
        int idx = toValidIndex(args.getTaskNumber(), tasks.size());
        String msg = replyMark(idx, markDone);
        storage.save(tasks);
        return msg;
    }

    private String deleteTaskAndSave(ParsedCommand parsed) throws SillyRatException, IOException {
        IndexArgs args = (IndexArgs) parsed.getArgs();
        int idx = toValidIndex(args.getTaskNumber(), tasks.size());
        String msg = replyDelete(idx);
        storage.save(tasks);
        return msg;
    }

    //region Responses
    private String replyList() {
        if (tasks.isEmpty()) {
            return "Your list is empty. Feed me tasks with todo/deadline/event.";
        }
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private String replyTodo(TodoArgs args) {
        Task task = new Todo(args.getDescription());
        tasks.add(task);
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

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

    private String replyDelete(int idx) {
        Task removed = tasks.remove(idx);
        return "Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

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
