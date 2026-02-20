package sillyrat.parser;

/**
 * Represents parsed command with associated arguments.
 */
public class ParsedCommand {
    private final Command command;
    private final Object args;

    /**
     * Constructor.
     * @param command The command enum value.
     * @param args The parsed arguments.
     */
    public ParsedCommand(Command command, Object args) {
        this.command = command;
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public Object getArgs() {
        return args;
    }

    public String getCommandWord() {
        return command.name().toLowerCase();
    }
}
