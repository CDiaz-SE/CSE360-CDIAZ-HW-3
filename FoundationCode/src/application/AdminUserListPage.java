package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.util.List;

public class AdminUserListPage {

	private final DatabaseHelper databaseHelper;
	private User user = null;

    public AdminUserListPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
    	
        TableView<User> tableView = new TableView<>();
        
        // Button to allow user to return to the previous screen
	    Button backButton = new Button("Back");
	    
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper, user).show(primaryStage);
        });

        // Columns
        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<User, List<String>> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> realNameCol = new TableColumn<>("Name");
        realNameCol.setCellValueFactory(new PropertyValueFactory<>("realName"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        tableView.getColumns().add(realNameCol);
        tableView.getColumns().add(emailCol);
        tableView.getColumns().add(userNameCol);
        tableView.getColumns().add(passwordCol);
        tableView.getColumns().add(roleCol);

        // Load data
        List<User> users = databaseHelper.getAllUsers();
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        tableView.setItems(observableUsers);
        
        TableColumn<User, Void> otpCol = new TableColumn<>("One-Time Password");
        
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button otpButton = new Button("Generate OTP");

                    {
                        otpButton.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            String otp = databaseHelper.generateOneTimePassword(selectedUser.getUserName());

                            // Show OneTimePass in a pop-up
                            Stage otpStage = new Stage();
                            Label otpLabel = new Label("One-Time Password for " + selectedUser.getUserName() + ": " + otp);
                            otpStage.setScene(new Scene(new VBox(10, otpLabel), 400, 100));
                            otpStage.setTitle("Generated OTP");
                            otpStage.show();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(otpButton);
                        }
                    }
                };
            }
        };

        otpCol.setCellFactory(cellFactory);

        tableView.getColumns().add(otpCol);
        
        // Modify user roles
        Label userNameLabel = new Label("Enter a user name");
        TextField userNameField = new TextField();
        Label roleLabel = new Label("Enter a role to add or remove");
        TextField roleField = new TextField();
        Button addRoleButton = new Button("Add Role");
        Button remRoleButton = new Button("Delete Role");
        addRoleButton.setOnAction(a -> {
        	databaseHelper.addUserRole(userNameField.getText(), roleField.getText());
        	List<User> newUsers = databaseHelper.getAllUsers();
            ObservableList<User> newObservableUsers = FXCollections.observableArrayList(newUsers);
            tableView.setItems(newObservableUsers);
        });
        remRoleButton.setOnAction(a -> {
        	databaseHelper.remUserRole(userNameField.getText(), roleField.getText());
        	List<User> newUsers = databaseHelper.getAllUsers();
            ObservableList<User> newObservableUsers = FXCollections.observableArrayList(newUsers);
            tableView.setItems(newObservableUsers);
        });
        
        
        VBox layout = new VBox(backButton, userNameLabel, userNameField, roleLabel, roleField, addRoleButton, remRoleButton, tableView);
        Scene scene = new Scene(layout, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("User List");
        primaryStage.show();
    }
}