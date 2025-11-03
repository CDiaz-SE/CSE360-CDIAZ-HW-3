package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordEvaluatorTest {

    @Test
    public void testValidPassword() {
        String result = PasswordEvaluator.evaluatePassword("Aa!15678");
        assertEquals("", result);
        assertTrue(PasswordEvaluator.foundUpperCase);
        assertTrue(PasswordEvaluator.foundLowerCase);
        assertTrue(PasswordEvaluator.foundNumericDigit);
        assertTrue(PasswordEvaluator.foundSpecialChar);
        assertTrue(PasswordEvaluator.foundLongEnough);
        
        result = PasswordEvaluator.evaluatePassword("Abcdef123!");
        assertEquals("", result);
    }

    @Test
    public void testNoUpperCase() {
        String result = PasswordEvaluator.evaluatePassword("abcdef123!");
        assertTrue(result.contains("Upper case"));
      
    }

    @Test
    public void testNoLowerCase() {
        String result = PasswordEvaluator.evaluatePassword("ABCD1234!");
        assertTrue(result.contains("Lower case"));
    }

    @Test
    public void testNoDigit() {
        String result = PasswordEvaluator.evaluatePassword("ABcd@EF");
        assertTrue(result.contains("Numeric digits"));
    }

    @Test
    public void testNoSpecialChar() {
        String result = PasswordEvaluator.evaluatePassword("ABcdEF1123");
        assertTrue(result.contains("Special character"));
    }

    @Test
    public void testTooShort() {
        String result = PasswordEvaluator.evaluatePassword("Ab1!");
        assertTrue(result.contains("Long Enough"));
    }

}
