package application;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Question class.
 */
public class QuestionTest {

    @Test
    public void testConstructorWithId() {
        Question q = new Question(1, "User1", "Title1", "Body1");
        
        assertEquals(1, q.getID());
        assertEquals("User1", q.getPoster());
        assertEquals("Title1", q.getTitle());
        assertEquals("Body1", q.getBody());
    }

    @Test
    public void testConstructorWithoutId() {
        Question q = new Question("User1", "Title1", "Body1");
        
        assertEquals("User1", q.getPoster());
        assertEquals("Title1", q.getTitle());
        assertEquals("Body1", q.getBody());
        assertEquals(0, q.getID());
    }

    @Test
    public void testSetAndAdd() {
        Question q = new Question("User1", "Title1", "Body1");
        
        q.setID(10);
        q.setPoster("User1");
        q.setTitle("Title2");
        q.setBody("Body3");
        
        assertEquals(10, q.getID());
        assertEquals("User1", q.getPoster());
        assertEquals("Title2", q.getTitle());
        assertEquals("Body3", q.getBody());
    }
}
