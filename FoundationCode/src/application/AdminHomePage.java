package application;

import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import databasePart1.*;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentAdmin;
	private final String role = "Admin";

    public AdminHomePage(DatabaseHelper databaseHelper, User currentAdmin) {
        this.databaseHelper = databaseHelper;
        this.currentAdmin = currentAdmin;
    }
	
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin! (" + currentAdmin.getUserName() + ")");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to allow user to return to the login screen
	    Button logoutButton = new Button("Log Out");
	    
	    Button viewUsersButton = new Button("User List");
	    viewUsersButton.setOnAction(e -> {
	        new AdminUserListPage(databaseHelper, currentAdmin).show(primaryStage);
	    });
	    
	    // "Invite" button for admin to generate invitation codes
	    Button inviteButton = new Button("Invite");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, currentAdmin, primaryStage);
        });
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    // Delete User Button
        Button deleteUserButton = new Button("Delete A User");
        deleteUserButton.setOnAction(e -> {
            List<String> users = databaseHelper.getAllUserNamesExcept(currentAdmin.getUserName());
            if (users.isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION, "No other users to delete.");
                alert.showAndWait();
                return;
            }
            // Create a choice dialog to select a user to delete
            ChoiceDialog<String> choice = new ChoiceDialog<>(users.get(0), users);
            choice.setTitle("Delete A User");
            choice.setHeaderText("Select a user to delete:");
            choice.setContentText("User:");
            Optional<String> result = choice.showAndWait();
            result.ifPresent(userToDelete -> {
                Alert confirm = new Alert(AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
            	confirm.setContentText("Are you sure you want to delete '" + userToDelete + "'?");
            	confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                Optional<ButtonType> confirmation = confirm.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.YES) {
                    boolean deleted = databaseHelper.deleteUser(userToDelete);
                    Alert done = new Alert(AlertType.INFORMATION, deleted ? "User deleted." : "Failed to delete user.");
                    
                    done.showAndWait();
                }
            });
        });
	    
	    layout.getChildren().addAll(adminLabel, viewUsersButton, deleteUserButton, inviteButton, logoutButton);
	    Scene adminScene = new Scene(layout, 800, 400);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}