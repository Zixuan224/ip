package sillyrat.parser;

import sillyrat.common.SillyRatException;

/**
 * Represent parser for user input
 */
public class Parser {

    /**
     * Parse user input and execute the corresponding command.
     * @param input User input
     * @return
     * @throws SillyRatException Exceptions for invalid input
     */
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

        case "find":
            return new ParsedCommand(commandWord, parseFindArgs(rest));

        default:
            throw new SillyRatException("I don't understand human language, Master. Speak in Ratinese: "
                        + "todo, deadline, event, list, mark, unmark, delete, find, bye");
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
            throw new SillyRatException("Event needs details. Example: event meeting /from 2026-05-05 14:00 /to 2026-05-05 16:00");
        }

        String[] fromSplit = rest.split(" /from ", 2);
        if (fromSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm");
        }

        String desc = fromSplit[0].trim();
        String afterFrom = fromSplit[1];

        String[] toSplit = afterFrom.split(" /to ", 2);
        if (toSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm");
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

    private FindArgs parseFindArgs(String rest) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Please provide a search term. Example: find book");
        }
        return new FindArgs(rest);
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