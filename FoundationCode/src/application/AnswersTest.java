package application;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswersTest {

    @Test
    public void testConstructorWithoutId() {
        Answer answer = new Answer(1, "User1", "answer1");

        assertEquals(1, answer.getQuestionID());
        assertEquals("User1", answer.getPoster());
        assertEquals("answer1", answer.getBody());
    }

    @Test
    public void testConstructorWithId() {
        Answer answer = new Answer(2, 1, "User2", "answer2");

        assertEquals(2, answer.getID());
        assertEquals(1, answer.getQuestionID());
        assertEquals("User2", answer.getPoster());
        assertEquals("answer2", answer.getBody());
    }

    @Test
    public void testSetAndGet() {
        Answer answer = new Answer(1, "User3", "answer3");

        answer.setID(5);
        answer.setPoster("User4");
        answer.setBody("answer4");

        assertEquals(5, answer.getID());
        assertEquals("User4", answer.getPoster());
        assertEquals("answer4", answer.getBody());
    }
}