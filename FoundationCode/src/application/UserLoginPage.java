package application;

import java.sql.SQLException;
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
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
            	User user=new User(userName, password, " ", " ", null);
            	WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            	
            	// Retrieve the user's role from the database using userName
            	List<String> role = databaseHelper.getUserRole(userName);
            	String email = databaseHelper.getEmail(userName);
            	String realName = databaseHelper.getRealName(userName);
            	
            	if(role!=null) {
            		user.setRole(role);
            		user.setEmail(email);
            		user.setRealName(realName);
            		
            		if(databaseHelper.login(user)) {
            			if (role.size() == 1) {
            				switch (role.getFirst()) {
            					case "admin": 
            						new AdminHomePage(databaseHelper,user).show(primaryStage);
            						break;
            					case "student":
            						new StudentHomePage(databaseHelper, user).show(primaryStage);
            						break;
            					case "reviewer":
            						new ReviewerHomePage(databaseHelper).show(primaryStage);
            						break;
            					case "instructor":
            						new InstructorHomePage(databaseHelper).show(primaryStage);
            						break;
            					case "staff":
            						new StaffHomePage(databaseHelper).show(primaryStage);
            						break;
            				}
            			}
            			else {
            				welcomeLoginPage.show(primaryStage,user);
            			}
            		}
            		else {
            			if (databaseHelper.validateOneTimePassword(userName, password)) {
            		        // OTP login success, force reset password
            		        new ResetPasswordPage(databaseHelper, userName).show(primaryStage);
            			} else
            			{
            				errorLabel.setText("Incorrect Password");
            			}
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("user account doesn't exists");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
