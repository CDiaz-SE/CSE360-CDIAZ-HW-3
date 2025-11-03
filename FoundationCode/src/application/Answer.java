package application;

/**
 * The Answer class represents an answer entity in the system.
 * It contains the answers details such as the original poster, the body of the answer, as well as the ID of the question the answer is replying to.
 */
public class Answer {
	private int id;
	private int questionID;
    private String poster;
    private String body;

    // Constructor to initialize a new Answer object with id, poster, and body.
    public Answer( int questionID, String poster, String body) {
        this.questionID = questionID;
    	this.poster = poster;
        this.body = body;
    }
    
    public Answer( int id, int questionID, String poster, String body) {
        this.id = id;
    	this.questionID = questionID;
    	this.poster = poster;
        this.body = body;
    }
    
    private boolean isSolution = false;

    public boolean isSolution() { return isSolution; }
    public void setSolution(boolean isSolution) { this.isSolution = isSolution; }
    
    public void setID(int id) {this.id = id;}
    public void setPoster(String poster) {this.poster = poster;}
    public void setBody(String body) {this.body = body;}
    
    public int getID() {return this.id;}
    public int getQuestionID() {return this.questionID;}
    public String getPoster() {return this.poster;}
    public String getBody() {return this.body;}
}