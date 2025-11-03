package application;

/**
 * The Question class represents a question entity in the system.
 * It contains the question's details such as the original poster, the question title, and body of the question, as well as a unique ID for each question.
 */
public class Question {
	private int id;
    private String poster;
    private String title;
    private String body;

    // Constructor to initialize a new Question object with id, poster, title, and body.
    public Question( int id, String poster, String title, String body) {
        this.id = id;
    	this.poster = poster;
        this.title = title;
        this.body = body;
    }
    
    // Constructor to initialize a new Question object with poster, title, and body.
    public Question( String poster, String title, String body) {
    	this.poster = poster;
        this.title = title;
        this.body = body;
    }
    
    private boolean isResolved;
    private boolean hasFollowUp;
    private Integer followUpQuestionId; // or int + flag

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean isResolved) { this.isResolved = isResolved; }

    public boolean hasFollowUp() { return hasFollowUp; }
    public void setHasFollowUp(boolean hasFollowUp) { this.hasFollowUp = hasFollowUp; }

    public Integer getFollowUpQuestionId() { return followUpQuestionId; }
    public void setFollowUpQuestionId(Integer id) { this.followUpQuestionId = id; }
    
    public void setID(int id) {this.id = id;}
    public void setPoster(String poster) {this.poster = poster;}
    public void setTitle(String title) {this.title = title;}
    public void setBody(String body) {this.body = body;}
    
    public int getID() {return this.id;}
    public String getPoster() {return this.poster;}
    public String getTitle() {return this.title;}
    public String getBody() {return this.body;}
}