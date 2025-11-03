package application;

import java.util.List;

/**
 * The Questions class represents a set of Question entities in the system.
 * It contains the a list of Question objects.
 */
public class Questions {
    private List<Question> questions;

    // Constructor to initialize a new Questions object with a list of Question objects.
    public Questions( List<Question> questions) {
        this.questions = questions;
    }
    
    // Sets the role of the user.
    public void setQuestions(List<Question> questions) {
    	this.questions = questions;
    }
    
    public void addQuestion(Question question) {
    	this.questions.add(question);
    }
    
    public void remQuestion(Question question) {
    	this.questions.remove(question);
    }

    public List<Question> getQuestions() { return this.questions; }

}
