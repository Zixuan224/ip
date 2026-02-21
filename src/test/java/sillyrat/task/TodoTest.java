package sillyrat.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TodoTest {

    @Test
    public void todo_toString_initiallyNotDone_hasTypeT() {
        Todo t = new Todo("read book");
        assertFalse(t.isDone());
        assertEquals("[T][ ] read book", t.toString());
    }

    @Test
    public void todo_markAndUnmark_updatesStatusIcon() {
        Todo t = new Todo("borrow cheese");

        t.markDone();
        assertTrue(t.isDone());
        assertEquals("[T][X] borrow cheese", t.toString());

        t.unmarkDone();
        assertFalse(t.isDone());
        assertEquals("[T][ ] borrow cheese", t.toString());
    }

    @Test
    public void todo_toSaveString_matchesExpectedFormat() {
        Todo t = new Todo("nap");
        assertEquals("T\t0\tnap", t.toSaveString());

        t.markDone();
        assertEquals("T\t1\tnap", t.toSaveString());
    }

    @Test
    public void task_getDescription_returnsOriginalText() {
        Todo t = new Todo("write tests");
        assertEquals("write tests", t.getDescription());
    }
}
