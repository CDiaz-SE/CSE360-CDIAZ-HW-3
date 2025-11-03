package application;

import databasePart1.DatabaseHelper;
import application.PasswordEvaluator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ResetPasswordPage allows a user who logged in with a one-time password
 * to set a new permanent password.
 */
public class ResetPasswordPage {

    private final DatabaseHelper databaseHelper;
    private final String userName;

    public ResetPasswordPage(DatabaseHelper databaseHelper, String userName) {
        this.databaseHelper = databaseHelper;
        this.userName = userName;
    }

    public void show(Stage primaryStage) {
        Label instructions = new Label("Enter a new password:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(a -> {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Passwords don't match.");
                return;
            }

            if (!PasswordEvaluator.isValid(newPassword)) {
                errorLabel.setText("Password must be at least 8 characters, "
                        + "with upper, lower, number, and special character.");
                return;
            }

            try {
                databaseHelper.updatePassword(userName, newPassword);
                databaseHelper.clearOneTimePassword(userName); // make OTP unusable
                // Redirect to login page
                new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (Exception e) {
                errorLabel.setText("Error resetting password.");
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, instructions, newPasswordField, confirmPasswordField, submitButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reset Password");
        primaryStage.show();
    }
}