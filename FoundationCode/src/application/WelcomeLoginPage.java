package application;

import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    List<String> role =user.getRole();
	    
	    
	    for (String i : role) {
	    	switch (i) {
	    		case "admin" :
	    			Button adminButton = new Button("Admin");
	    			adminButton.setOnAction(a -> {
	    				new AdminHomePage(databaseHelper, user).show(primaryStage);
	    			});
	    			layout.getChildren().add(adminButton);
	    			break;
	    		case "student" :
	    			Button studentButton = new Button("Student");
	    			studentButton.setOnAction(a -> {
	    				new StudentHomePage(databaseHelper, user).show(primaryStage);
	    			});
	    			layout.getChildren().add(studentButton);
	    			break;
	    		case "reviewer" :
	    			Button reviewerButton = new Button("Reviewer");
	    			reviewerButton.setOnAction(a -> {
	    				new ReviewerHomePage(databaseHelper).show(primaryStage);
	    			});
	    			layout.getChildren().add(reviewerButton);
	    			break;
	    		case "instructor" :
	    			Button instructorButton = new Button("Instructor");
	    			instructorButton.setOnAction(a -> {
	    				new InstructorHomePage(databaseHelper).show(primaryStage);
	    			});
	    			layout.getChildren().add(instructorButton);
	    			break;
	    		case "staff" :
	    			Button staffButton = new Button("Staff");
	    			staffButton.setOnAction(a -> {
	    				new StaffHomePage(databaseHelper).show(primaryStage);
	    			});
	    			layout.getChildren().add(staffButton);
	    			break;
	    	}
	    }
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

	    layout.getChildren().addAll(welcomeLabel,/*continueButton,*/quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}