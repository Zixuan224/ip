package sillyrat.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for parsing and formatting date/time strings.
 */

public class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm");

    private static final DateTimeFormatter STORAGE =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter[] USER_DATE_TIME_FORMATS = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
    };

    private static final DateTimeFormatter[] USER_DATE_ONLY_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
    };

    private DateTimeUtil() {
    }

    /**
     * Parses a user-provided date/time string into a LocalDateTime object.
     *
     * @param raw The date/time string to parse.
     * @return The parsed LocalDateTime object.
     * @throws IllegalArgumentException If the input string does not match any supported format.
     */
    public static LocalDateTime parseUserDateTime(String raw) {
        String s = raw.trim();

        for (DateTimeFormatter f : USER_DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter f : USER_DATE_ONLY_FORMATS) {
            try {
                LocalDate d = LocalDate.parse(s, f);
                return d.atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException(
                "Invalid date/time format.\n"
                        + "Accepted formats:\n"
                        + "  - yyyy-MM-dd (e.g., 2019-10-15)\n"
                        + "  - yyyy-MM-dd HH:mm (e.g., 2019-10-15 18:00)\n"
                        + "  - d/M/yyyy HHmm (e.g., 2/12/2019 1800 means 2 Dec 2019 18:00)"
        );
    }

    /**
     * Formats a LocalDateTime object into a human-readable string.
     * @param dt The LocalDateTime object to format.
     * @return The formatted string representing the date and time.
     */
    public static String toDisplayString(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.toLocalDate().format(DISPLAY_DATE);
        }
        return dt.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats a LocalDateTime object into a storage-friendly string.
     * @param dt The LocalDateTime object to format.
     * @return The formatted string suitable for storage.
     */
    public static String toStorageString(LocalDateTime dt) {
        return dt.format(STORAGE);
    }

    /**
     * Parses a storage-friendly string into a LocalDateTime object.
     * @param raw The storage-friendly string to parse.
     * @return The parsed LocalDateTime object.
     */
    public static LocalDateTime parseStorageDateTime(String raw) {
        return LocalDateTime.parse(raw.trim(), STORAGE);
    }
}
