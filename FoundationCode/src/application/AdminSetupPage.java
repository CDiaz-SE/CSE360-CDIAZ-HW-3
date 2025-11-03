package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
    	Label userNameLabel = new Label("Admin Username:");
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        
    	Label emailLabel = new Label("Email:");
    	TextField emailField = new TextField();
    	emailField.setPromptText("Enter Email");
    	emailField.setMaxWidth(250);
        
        Button setupButton = new Button("Setup");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String userNameCheckInfo = UserNameRecognizer.checkForValidUserName(userName);
            String passwordCheckInfo = PasswordEvaluator.evaluatePassword(password);
            if (userNameCheckInfo == "") {
            	if (passwordCheckInfo == "") {
		            try {
		            	// Create a new User object with admin role and register in the database
		            	List<String> roles = new ArrayList<>();
		            	roles.add("admin");
		            	User user=new User(userName, password, "admin", email, roles);
		                databaseHelper.register(user);
		                System.out.println("Administrator setup completed.");
		                
		                // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
		            } catch (SQLException e) {
		                System.err.println("Database error: " + e.getMessage());
		                e.printStackTrace();
		            }
            	} else {
            		errorLabel.setText("Password Error: " + passwordCheckInfo);
            	}
            } else {
            	errorLabel.setText("UserName Error: " +userNameCheckInfo);
            }
        });

        VBox layout = new VBox(10, userNameLabel, userNameField, passwordLabel, passwordField, emailLabel, emailField, setupButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
