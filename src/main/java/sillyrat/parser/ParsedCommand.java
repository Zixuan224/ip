package sillyrat.parser;

public class ParsedCommand {
    private final String commandWord;
    private final Object args;

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