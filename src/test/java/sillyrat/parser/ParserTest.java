package sillyrat.parser;

import org.junit.jupiter.api.Test;
import sillyrat.common.SillyRatException;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    private final Parser parser = new Parser();

    @Test
    public void parse_nullOrBlank_throws() {
        assertThrows(SillyRatException.class, () -> parser.parse(null));
        assertThrows(SillyRatException.class, () -> parser.parse(""));
        assertThrows(SillyRatException.class, () -> parser.parse("   "));
        assertThrows(SillyRatException.class, () -> parser.parse("\n\t"));
    }

    @Test
    public void parse_listWithExtraArgs_throws() {
        SillyRatException ex = assertThrows(SillyRatException.class, () -> parser.parse("list now"));
        assertTrue(ex.getMessage().toLowerCase().contains("list"));
    }

    @Test
    public void parse_bye_noArgs_ok() throws Exception {
        ParsedCommand cmd = parser.parse("bye");
        assertEquals("bye", cmd.getCommandWord());
        assertNotNull(cmd.getArgs());
    }

    @Test
    public void parse_todo_missingDescription_throws() {
        assertThrows(SillyRatException.class, () -> parser.parse("todo"));
        assertThrows(SillyRatException.class, () -> parser.parse("todo   "));
    }

    @Test
    public void parse_deadline_missingByDelimiter_throws() {
        assertThrows(SillyRatException.class, () -> parser.parse("deadline return book"));
        assertThrows(SillyRatException.class, () -> parser.parse("deadline return book /by"));
        assertThrows(SillyRatException.class, () -> parser.parse("deadline  /by Sunday"));
    }

    @Test
    public void parse_mark_invalidIndex_throws() {
        assertThrows(SillyRatException.class, () -> parser.parse("mark"));
        assertThrows(SillyRatException.class, () -> parser.parse("mark 0"));
        assertThrows(SillyRatException.class, () -> parser.parse("mark -1"));
        assertThrows(SillyRatException.class, () -> parser.parse("mark two"));
    }

    @Test
    public void parse_unknownCommand_throws() {
        SillyRatException ex = assertThrows(SillyRatException.class, () -> parser.parse("abracadabra"));
        assertTrue(ex.getMessage().toLowerCase().contains("don't understand")
                || ex.getMessage().toLowerCase().contains("dont understand"));
    }
}
