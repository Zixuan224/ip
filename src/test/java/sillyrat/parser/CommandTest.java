package sillyrat.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    @Test
    public void fromString_isCaseInsensitive() {
        assertEquals(Command.LIST, Command.fromString("list"));
        assertEquals(Command.LIST, Command.fromString("LIST"));
        assertEquals(Command.LIST, Command.fromString("LiSt"));
    }

    @Test
    public void fromString_unknown_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Command.fromString("abracadabra"));
    }
}
