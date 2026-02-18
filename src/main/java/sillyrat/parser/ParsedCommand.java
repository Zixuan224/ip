package sillyrat.parser;

/**
 * Represents parsed command with associated arguments.
 */
public class ParsedCommand {
    private final String commandWord;
    private final Object args;

    /**
     * Constructor.
     * @param commandWord
     * @param args
     */
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
