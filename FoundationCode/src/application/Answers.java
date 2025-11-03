package application;

import java.util.List;

/**
 * The Answer class represents a set of Answer entities in the system.
 * It contains the a list of Question Answer.
 */
public class Answers {
    private List<Answer> answers;

    // Constructor to initialize a new Answers object with a list of Answer objects.
    public Answers( List<Answer> answers) {
        this.answers = answers;
    }
    
    // Sets the role of the user.
    public void setAnswer(List<Answer> answers) {
    	this.answers = answers;
    }
    
    public void addAnswer(Answer answer) {
    	this.answers.add(answer);
    }
    
    public void remAnswer(Answer answer) {
    	this.answers.remove(answer);
    }

    public List<Answer> getAnswers() { return this.answers; }

}