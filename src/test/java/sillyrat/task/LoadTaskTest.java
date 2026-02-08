package sillyrat.task;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LoadTaskTest {

    @Test
    public void toLoadTask_todo_notDone() {
        Task t = Task.toLoadTask("T\t0\tread book");
        assertTrue(t instanceof Todo);
        assertFalse(t.isDone());
        assertEquals("[T][ ] read book", t.toString());
        assertEquals("T\t0\tread book", t.toSaveString());
    }

    @Test
    public void toLoadTask_deadline_done() {
        Task t = Task.toLoadTask("D\t1\tsubmit report\t2019-10-15T18:00");
        assertTrue(t instanceof Deadline);
        assertTrue(t.isDone());

        Deadline d = (Deadline) t;
        assertNotNull(d.getBy());
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), d.getBy());

        // Save string should match what DateTimeUtil.toStorageString produces (includes seconds)
        assertEquals("D\t1\tsubmit report\t2019-10-15T18:00:00", d.toSaveString());
    }

    @Test
    public void toLoadTask_event_notDone() {
        Task t = Task.toLoadTask("E\t0\tmeeting\t2019-10-15T14:00\t2019-10-15T16:00");
        assertTrue(t instanceof Event);
        assertFalse(t.isDone());

        Event e = (Event) t;
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0), e.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 16, 0), e.getTo());
        assertEquals("E\t0\tmeeting\t2019-10-15T14:00:00\t2019-10-15T16:00:00", e.toSaveString());
    }

    @Test
    public void toLoadTask_unknownType_fallsBackToPlainTask() {
        Task t = Task.toLoadTask("Z\t0\tsomething");
        assertEquals("[ ][ ] something", t.toString());
        assertEquals(" \t0\tsomething", t.toSaveString());
    }
}