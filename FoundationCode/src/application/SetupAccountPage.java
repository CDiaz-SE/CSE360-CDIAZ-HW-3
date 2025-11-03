package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
    	Label userNameLabel = new Label("Username:");
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

    	Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        Label realNameLabel = new Label("Real Name:");
    	TextField realNameField = new TextField();
    	realNameField.setPromptText("Enter Real Name");
    	realNameField.setMaxWidth(250);
        
    	Label emailLabel = new Label("Email:");
    	TextField emailField = new TextField();
    	emailField.setPromptText("Enter Email");
    	emailField.setMaxWidth(250);
        
        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("student", "reviewer", "instructor", "staff");
        roleDropdown.setValue("student");
        roleDropdown.setMaxWidth(250);
        
        Label invitationCodeLabel = new Label("InvitationCode:");
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String realName = realNameField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();
            String role = roleDropdown.getValue();
            List<String> roles = new ArrayList<>();
            roles.add(role);
            
            String userNameCheckInfo = UserNameRecognizer.checkForValidUserName(userName);
            String passwordCheckInfo = PasswordEvaluator.evaluatePassword(password);
            
            if (userNameCheckInfo == "") {
            	if (passwordCheckInfo == "") {
		            
		            try {
		            	// Check if the user already exists
		            	if(!databaseHelper.doesUserExist(userName)) {
		            		
		            		// Validate the invitation code
		            		if(databaseHelper.validateInvitationCode(code)) {
//<<<<<<< HEAD

                      
		            			//Check the invitation code expiration
		            			if(databaseHelper.CheckInvitationCodeExpiration(code)) {
		            				
		            				// Create a new user and register them in the database
					            	User user=new User(userName, password, email, realName, roles);
					              databaseHelper.register(user);
					                
					              // Navigate to the Welcome Login Page
					              new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
		            			}
		            			else {
			            			errorLabel.setText("Invite code has expired. Please reqest a new invite");
			            		}
		            		}
		            		else {
		            			errorLabel.setText("Please enter a valid invitation code");
		            		}
		            	}
		            	else {
		            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
		            	}
		            	
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

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameLabel, userNameField, passwordLabel, passwordField, emailLabel, emailField, realNameLabel, realNameField,
        		roleLabel, roleDropdown, invitationCodeLabel, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 450));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}