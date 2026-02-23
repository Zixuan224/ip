package sillyrat.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

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
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT)
    };

    private static final DateTimeFormatter[] USER_DATE_ONLY_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT)
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
                LocalDateTime dt = LocalDateTime.parse(s, f);

                if (dt.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Date/time cannot be in the past.");
                }
                return dt;
            } catch (Exception ignored) {
            }
        }

        for (DateTimeFormatter f : USER_DATE_ONLY_FORMATS) {
            try {
                LocalDate d = LocalDate.parse(s, f);

                if (d.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Date cannot be before today.");
                }
                return d.atStartOfDay();
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException(
                "Invalid date/time format.\n"
                        + "Accepted formats:\n"
                        + "  - YYYY-MM-DD (e.g., 2019-10-15)\n"
                        + "  - YYYY-MM-DD HHmm (e.g., 2019-10-15 1800)\n"
                        + "  - D/M/YYYY HHmm (e.g., 2/12/2019 1800 means 2 Dec 2019 18:00)\n"
                        + "Also: dates/times must not be in the past."
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
