package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UserNameRecognizerTest {

    @Test
    public void testEmptyInput() {
        String result = UserNameRecognizer.checkForValidUserName("");
        assertTrue(result.contains("empty"));
    }

    @Test
    public void testStartsWithDigit() {
        String result = UserNameRecognizer.checkForValidUserName("1user");
        assertTrue(result.contains("must start with A-Z"));
    }

    @Test
    public void testTooShort() {
        String result = UserNameRecognizer.checkForValidUserName("Ab1");
        assertTrue(result.contains("A UserName must have at least 4 characters"));
    }

    @Test
    public void testTooLong() {
        String result = UserNameRecognizer.checkForValidUserName("abcdefghijklmnopq1");
        assertTrue(result.contains("A UserName must have no more than 16 character"));
    }

    @Test
    public void testInvalidAfterSpecialChar() {
        String result = UserNameRecognizer.checkForValidUserName("ABcdef._");
        assertTrue(result.contains("A UserName character after a period/minus/under_score must be A-Z"));
    }

    @Test
    public void testValidSimpleUserName() {
        String result = UserNameRecognizer.checkForValidUserName("Yang_1360");
        assertEquals("", result);
        assertEquals(-1, UserNameRecognizer.userNameRecognizerIndexofError);
    }

}
