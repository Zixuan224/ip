package sillyrat;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SillyRatTest {

    @TempDir
    Path tempDir;

    private SillyRat newBot() {
        Path file = tempDir.resolve("data").resolve("silly-rat.txt");
        return new SillyRat(file.toString());
    }

    @Test
    public void getResponse_listInitiallyEmpty_showsEmptyMessage() {
        SillyRat bot = newBot();
        String resp = bot.getResponse("list");
        assertTrue(resp.toLowerCase().contains("nothing on the list"));
    }

    @Test
    public void getResponse_todoThenList_showsAddedTask() {
        SillyRat bot = newBot();

        String add = bot.getResponse("todo read book");
        assertTrue(add.toLowerCase().contains("added this task"));
        assertTrue(add.contains("read book"));

        String list = bot.getResponse("list");
        assertTrue(list.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_markAndUnmark_updatesTaskState() {
        SillyRat bot = newBot();
        bot.getResponse("todo borrow cheese");

        String mark = bot.getResponse("mark 1");
        assertTrue(mark.contains("[T][X] borrow cheese"));

        String unmark = bot.getResponse("unmark 1");
        assertTrue(unmark.contains("[T][ ] borrow cheese"));
    }

    @Test
    public void getResponse_markAlreadyDone_returnsFriendlyMessage() {
        SillyRat bot = newBot();
        bot.getResponse("todo nap");
        bot.getResponse("mark 1");

        String markAgain = bot.getResponse("mark 1");
        assertTrue(markAgain.toLowerCase().contains("already"));
    }

    @Test
    public void getResponse_delete_removesTask() {
        SillyRat bot = newBot();
        bot.getResponse("todo a");
        bot.getResponse("todo b");

        String del = bot.getResponse("delete 1");
        assertTrue(del.toLowerCase().contains("removed"));
        assertTrue(del.contains("a"));

        String list = bot.getResponse("list");
        assertFalse(list.contains("1. [T][ ] a"));
        assertTrue(list.contains("1. [T][ ] b"));
    }

    @Test
    public void getResponse_find_returnsMatchingTasksOnly() {
        SillyRat bot = newBot();
        bot.getResponse("todo read book");
        bot.getResponse("todo return book");
        bot.getResponse("todo buy milk");

        String found = bot.getResponse("find book");
        assertTrue(found.toLowerCase().contains("matching tasks"));
        assertTrue(found.contains("read book"));
        assertTrue(found.contains("return book"));
        assertFalse(found.contains("buy milk"));
    }

    @Test
    public void getResponse_deadline_withInvalidDate_showsErrorMessage() {
        SillyRat bot = newBot();
        String resp = bot.getResponse("deadline submit /by not-a-date");
        assertTrue(resp.toLowerCase().contains("invalid date/time format"));
    }

    @Test
    public void getResponse_event_endBeforeStart_showsErrorMessage() {
        SillyRat bot = newBot();
        String resp = bot.getResponse("event meeting /from 2026-05-05 1600 /to 2026-05-05 1400");
        assertTrue(resp.toLowerCase().contains("end"));
        assertTrue(resp.toLowerCase().contains("start"));
    }

    @Test
    public void getStartupReminder_whenNoUpcomingTasks_hasChillMessage() {
        SillyRat bot = newBot();
        String reminder = bot.getStartupReminder();
        assertTrue(reminder.toLowerCase().contains("no upcoming"));
    }

    @Test
    public void getStartupReminder_whenUpcomingDeadlineInNext7Days_listsIt() {
        SillyRat bot = newBot();

        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        bot.getResponse("deadline submit report /by " + tomorrow);

        String reminder = bot.getStartupReminder();
        assertTrue(reminder.toLowerCase().contains("reminder"));
        assertTrue(reminder.contains("submit report"));
    }

    @Test
    public void getResponse_unknownCommand_returnsHelpTextNotCrash() {
        SillyRat bot = newBot();
        String resp = bot.getResponse("abracadabra");
        assertTrue(resp.toLowerCase().contains("don't understand")
                || resp.toLowerCase().contains("dont understand"));
    }

    @Test
    public void getResponse_blankInput_returnsParserMessage() {
        SillyRat bot = newBot();
        String resp = bot.getResponse("   ");
        assertTrue(resp.toLowerCase().contains("say something"));
    }
}
