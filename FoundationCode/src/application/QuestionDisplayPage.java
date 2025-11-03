package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class QuestionDisplayPage {

	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	private final int qId;

	public QuestionDisplayPage(DatabaseHelper databaseHelper, User user, int qId) {
		this.databaseHelper = databaseHelper;
		this.currentUser = user;
		this.qId = qId;
	}

	public void show(Stage primaryStage) throws SQLException {
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 15;");

	    Question q = databaseHelper.getQuestionById(this.qId); 

	    Label titleLabel = new Label("Question " + q.getID() + " — " + q.getTitle());
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    // Style title if resolved
	    if (q.isResolved()) {
	        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #eaffea; -fx-padding: 6;");
	    }

	    Label bodyLabel = new Label(q.getBody());
	    bodyLabel.setWrapText(true);

	    Label statusLabel = new Label(q.isResolved() ? "✅ Resolved" : "❓ Unresolved");
	    statusLabel.setStyle("-fx-text-fill: " + (q.isResolved() ? "green;" : "red;"));

	    layout.getChildren().addAll(titleLabel, bodyLabel, statusLabel);

	    // Show follow-up indicator and button if present
	    if (q.hasFollowUp()) {
	        Label fuLabel = new Label("This question has a follow-up.");
	        fuLabel.setStyle("-fx-text-fill: #1f618d; -fx-font-style: italic;");
	        layout.getChildren().add(fuLabel);

	        Integer fuId = q.getFollowUpQuestionId();
	        if (fuId != null) {
	            Button viewFollowUp = new Button("View Follow-Up");
	            viewFollowUp.setOnAction(e -> {
	                try {
	                    new QuestionDisplayPage(databaseHelper, currentUser, fuId).show(primaryStage);
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });
	            layout.getChildren().add(viewFollowUp);
	        }
	    }

	    List<Answer> answers = databaseHelper.getAnswers(q.getID());

	    for (Answer a : answers) {
	        VBox answerBox = new VBox(5);
	        answerBox.setStyle("-fx-padding: 5; -fx-border-color: lightgray; -fx-border-width: 1;");

	        Label ansLabel = new Label("Answer " + a.getID() + " by " + a.getPoster() + ": " + a.getBody());
	        ansLabel.setWrapText(true);

	        // If answer is marked solution
	        if (a.isSolution()) {
	            // Make the solution stand out (yellow background)
	            answerBox.setStyle("-fx-padding: 5; -fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #fff7cc;");
	            Label solutionLabel = new Label("✔ Marked as Solution");
	            solutionLabel.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
	            answerBox.getChildren().addAll(ansLabel, solutionLabel);
	        } else {
	            answerBox.getChildren().add(ansLabel);
	            // Show button if current user is the question poster and question is NOT resolved
	            if (q.getPoster().equals(currentUser.getUserName()) && !q.isResolved()) {
	                Button markSolutionButton = new Button("Mark as Solution");
	                markSolutionButton.setOnAction(e -> {
	                    databaseHelper.markAnswerAsSolution(q.getID(), a.getID());
	                    try {
	                        // Re-open this page to refresh state
	                        new QuestionDisplayPage(databaseHelper, currentUser, this.qId).show(primaryStage);
	                    } catch (SQLException ex) {
	                        ex.printStackTrace();
	                    }
	                });
	                answerBox.getChildren().add(markSolutionButton);
	            }
	        }
	       
	        layout.getChildren().add(answerBox);
	    }

	    // follow-up section (visible only to poster)
	    if (q.getPoster().equals(currentUser.getUserName())) {
	        Label followUpLabel = new Label("Post a Follow-Up Question");
	        TextField followUpTitleField = new TextField();
	        followUpTitleField.setPromptText("Enter follow-up title");
	        TextArea followUpBodyArea = new TextArea();
	        followUpBodyArea.setPromptText("Enter follow-up body text...");
	        followUpBodyArea.setPrefRowCount(3);

	        Button postFollowUpButton = new Button("Post Follow-Up");
	        postFollowUpButton.setOnAction(e -> {
	            try {
	                databaseHelper.addFollowUpQuestion(q.getID(), currentUser.getUserName(),
	                        followUpTitleField.getText(), followUpBodyArea.getText());
	                // refresh the page after insertion
	                new QuestionDisplayPage(databaseHelper, currentUser, this.qId).show(primaryStage);
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        });

	        layout.getChildren().addAll(followUpLabel, followUpTitleField, followUpBodyArea, postFollowUpButton);
	    }

	    Button backButton = new Button("Back");
	    backButton.setOnAction(event -> {
	        new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
	    });

	    layout.getChildren().add(backButton);
	    Scene scene = new Scene(layout, 800, 600);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Question & Answers");
	    primaryStage.show();
	}

	}