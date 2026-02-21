package sillyrat.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedCommandTest {

    @Test
    public void getCommandWord_lowerCasesEnumName() {
        ParsedCommand pc = new ParsedCommand(Command.DEADLINE, new NoArgs());
        assertEquals("deadline", pc.getCommandWord());
        assertEquals(Command.DEADLINE, pc.getCommand());
        assertNotNull(pc.getArgs());
    }
}
