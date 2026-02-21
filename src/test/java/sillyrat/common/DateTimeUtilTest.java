package sillyrat.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeUtilTest {

    @Test
    public void parseUserDateTime_acceptsDateOnly_returnsStartOfDay() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2019-10-15");
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), dt);
    }

    @Test
    public void parseUserDateTime_acceptsIsoDateTimeWithHHmm() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2019-10-15 1800");
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), dt);
    }

    @Test
    public void parseUserDateTime_acceptsSlashFormat() {
        LocalDateTime dt = DateTimeUtil.parseUserDateTime("2/12/2019 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dt);
    }

    @Test
    public void parseUserDateTime_invalid_throwsHelpfulMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> DateTimeUtil.parseUserDateTime("not-a-date"));
        assertTrue(ex.getMessage().contains("Accepted formats"));
    }

    @Test
    public void toDisplayString_midnight_showsOnlyDate() {
        String s = DateTimeUtil.toDisplayString(LocalDateTime.of(2019, 10, 15, 0, 0));
        assertEquals("Oct 15 2019", s);
    }

    @Test
    public void toDisplayString_nonMidnight_showsDateAndTime() {
        String s = DateTimeUtil.toDisplayString(LocalDateTime.of(2019, 10, 15, 18, 5));
        assertEquals("Oct 15 2019 18:05", s);
    }

    @Test
    public void toStorageString_and_parseStorageDateTime_roundTrip() {
        LocalDateTime original = LocalDateTime.of(2019, 10, 15, 18, 0, 0);
        String stored = DateTimeUtil.toStorageString(original);
        assertEquals(original, DateTimeUtil.parseStorageDateTime(stored));
    }

    @Test
    public void parseStorageDateTime_trimsWhitespace() {
        LocalDateTime dt = DateTimeUtil.parseStorageDateTime(" 2019-10-15T18:00:00 \n");
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0, 0), dt);
    }
}
