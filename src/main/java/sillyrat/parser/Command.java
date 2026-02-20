package sillyrat.parser;

/**
 * Enumerates all recognised commands.
 */
public enum Command {
    LIST, BYE, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, FIND, REMIND;

    /**
     * Converts a command word string to a Command enum value.
     *
     * @param word The command word.
     * @return The matching Command.
     * @throws IllegalArgumentException If the word does not match any command.
     */
    public static Command fromString(String word) {
        try {
            return Command.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown command: " + word);
        }
    }
}
