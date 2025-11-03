package application;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for the Questions class.
 */
public class QuestionsTest {

    @Test
    public void testConstructorAndGetQuestions() {
    	Question q1 = new Question(1, "User1", "Title1", "Body1");
    	Question q2 = new Question(2, "User2", "Title2", "Body2");
    	List<Question> initialList = new ArrayList<>();
        initialList.add(q1);
        Questions questions = new Questions(initialList);
        List<Question> list = questions.getQuestions();
        assertEquals(1, list.size());
        assertTrue(list.contains(q1));
    }

    @Test
    public void testAddQuestion() {
    	Question q1 = new Question(1, "User1", "Title1", "Body1");
    	Question q2 = new Question(2, "User2", "Title2", "Body2");
    	List<Question> initialList = new ArrayList<>();
        initialList.add(q1);
        Questions questions = new Questions(initialList);
        List<Question> list = questions.getQuestions();
        assertEquals(1, list.size());
        assertTrue(list.contains(q1));
        
        questions.addQuestion(q2);
        List<Question> list2 = questions.getQuestions();
        assertEquals(2, list2.size());
        assertTrue(list2.contains(q2));
    }

    @Test
    public void testRemoveQuestion() {
    	Question q1 = new Question(1, "User1", "Title1", "Body1");
    	Question q2 = new Question(2, "User2", "Title2", "Body2");
    	List<Question> initialList = new ArrayList<>();
        initialList.add(q1);
        initialList.add(q2);
        Questions questions = new Questions(initialList);
        List<Question> list = questions.getQuestions();
        assertEquals(2, list.size());
        assertTrue(list.contains(q1));
        questions.remQuestion(q1);
        assertEquals(1, list.size());
        assertFalse(list.contains(q1));
        assertTrue(list.contains(q2));
    }

    @Test
    public void testSetQuestionsReplacesList() {
    	Question q1 = new Question(1, "User1", "Title1", "Body1");
    	Question q2 = new Question(2, "User2", "Title2", "Body2");
    	List<Question> initialList = new ArrayList<>();
        initialList.add(q1);
        initialList.add(q2);
        Questions questions = new Questions(initialList);
        List<Question> list = questions.getQuestions();
        assertEquals(2, list.size());
        assertTrue(list.contains(q1));
        
        List<Question> newList = new ArrayList<>();
        Question q3 = new Question(3, "User3", "Title3", "Body3");
        newList.add(q3);

        questions.setQuestions(newList);
        list = questions.getQuestions();

        assertEquals(1, list.size());
        assertTrue(list.contains(q3));
        assertFalse(list.contains(q1));
    }
}