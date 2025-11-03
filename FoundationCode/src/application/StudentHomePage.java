package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class StudentHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	private final String role = "Student";
	
	public StudentHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
    }

    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, " + currentUser.getUserName());
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Button forum = new Button("Forum");
	    forum.setOnAction(event -> {
	    	new ForumPage(databaseHelper, currentUser, role).show(primaryStage);
	    });
	    
	    // Button to allow user to return to the login screen
	    Button logoutButton = new Button("Log Out");
	    
	    logoutButton.setOnAction(event -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(userLabel, forum, logoutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
	    primaryStage.show();
    	
    }
}