package sillyrat.parser;

import sillyrat.common.SillyRatException;

/**
 * Parses raw user input strings into structured {@link ParsedCommand} objects.
 */
public class Parser {

    /**
     * Parses the given user input and returns a structured command with its arguments.
     *
     * @param input The raw user input string.
     * @return The parsed command containing the command type and arguments.
     * @throws SillyRatException If the input is empty or contains an unrecognized command.
     */
    public ParsedCommand parse(String input) throws SillyRatException {
        if (input == null || input.trim().isEmpty()) {
            throw new SillyRatException("Say something, Master. My tiny ears heard nothing.");
        }

        String trimmed = input.trim();
        assert !trimmed.isEmpty() : "Trimmed input should not be empty after validation";

        String[] parts = trimmed.split(" ", 2);
        assert parts.length >= 1 : "Split should always produce at least one element";

        String commandWord = parts[0];
        String rest = (parts.length > 1) ? parts[1].trim() : "";

        Command command;
        try {
            command = Command.fromString(commandWord);
        } catch (IllegalArgumentException e) {
            throw new SillyRatException("I don't understand Meowese, Master. "
                    + "\n\nTalk in Squeakese:"
                    + "\n• New task: todo, deadline, event"
                    + "\n• Manage: list, mark, unmark, delete"
                    + "\n• Search: find"
                    + "\n• Reminders: remind");
        }

        switch (command) {
        case LIST:
        case BYE:
        case REMIND:
            requireNoArgs(commandWord, rest);
            return new ParsedCommand(command, new NoArgs());

        case TODO:
            return new ParsedCommand(command, parseTodoArgs(rest));

        case DEADLINE:
            return new ParsedCommand(command, parseDeadlineArgs(rest));

        case EVENT:
            return new ParsedCommand(command, parseEventArgs(rest));

        case MARK:
        case UNMARK:
        case DELETE:
            return new ParsedCommand(command, parseIndexArgs(commandWord, rest));

        case FIND:
            return new ParsedCommand(command, parseFindArgs(rest));

        default:
            throw new SillyRatException("I don't understand Meowese, Master. "
                    + "\n\nTalk in Squeakese:"
                    + "\n• New task: todo, deadline, event"
                    + "\n• Manage: list, mark, unmark, delete"
                    + "\n• Search: find"
                    + "\n• Reminders: remind");
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
            throw new SillyRatException("Deadline needs details. Example: deadline submit report /by 2026-05-05 1600");
        }

        String[] split = rest.split(" /by ", 2);
        if (split.length < 2) {
            throw new SillyRatException("Deadline format: deadline <task> /by yyyy-MM-dd HHmm");
        }

        String desc = split[0].trim();
        String byRaw = split[1].trim();

        if (desc.isEmpty()) {
            throw new SillyRatException(
                    "Deadline description cannot be empty. "
                            + "Example: deadline return book /by 2026-05-05 1600");
        }
        if (byRaw.isEmpty()) {
            throw new SillyRatException("Deadline time cannot be empty. "
                    + "Example: deadline return book /by 2026-05-05 1600");
        }

        return new DeadlineArgs(desc, byRaw);
    }

    private EventArgs parseEventArgs(String rest) throws SillyRatException {
        if (rest.isEmpty()) {
            throw new SillyRatException("Event needs details."
                    + " Example: event meeting /from 2026-05-05 1400 /to 2026-05-05 1600");
        }

        String[] fromSplit = rest.split(" /from ", 2);
        if (fromSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        }

        String desc = fromSplit[0].trim();
        String afterFrom = fromSplit[1];

        String[] toSplit = afterFrom.split(" /to ", 2);
        if (toSplit.length < 2) {
            throw new SillyRatException("Event format: event <task> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
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

    private FindArgs parseFindArgs(String rest)
            throws SillyRatException {
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
