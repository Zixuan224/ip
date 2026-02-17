package sillyrat;

// 1. ADD THESE IMPORTS
import sillyrat.common.DateTimeUtil;
import sillyrat.common.SillyRatException;
import sillyrat.parser.*; // Imports Parser, ParsedCommand, and all Args classes
import sillyrat.storage.Storage;
import sillyrat.task.*;   // Imports Task, TaskList, Todo, Deadline, Event
import sillyrat.ui.Ui;

import java.io.IOException;
import java.util.List;

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

        case "find": {
            FindArgs args = (FindArgs) parsed.getArgs();
            doFind(args, tasks);
            return false;
        }

            default:
                throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: "
                        + "todo, deadline, event, list, mark, unmark, delete, find, bye");
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
        java.time.LocalDateTime by;
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
        java.time.LocalDateTime from;
        java.time.LocalDateTime to;
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
            // 2. CHANGED t.isDone to t.isDone()
            if (t.isDone()) {
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

        // 3. CHANGED t.isDone to t.isDone()
        if (!t.isDone()) {
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