package application;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.time.LocalDateTime;
import java.util.List;

import databasePart1.DatabaseHelper;

public class ForumPage {
	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	String userRole = null;
	int qId;
	int aId;
	
	public ForumPage(DatabaseHelper databaseHelper, User user, String role) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
        this.userRole = role;
    }
	
	public void show(Stage primaryStage) {
		// Misc
	    ListView<Answer> aListView = new ListView<>();
		VBox.setVgrow(aListView, Priority.ALWAYS);
	    List<Question> questions = null;
		
		// Buttons
		Button back = new Button("Back");
		Button newQuestion = new Button("New Question");
		Button postQuestion = new Button("Ask");
		Button cancelQuestion = new Button("Cancel");
		Button updateQuestion = new Button("Update");
		Button postAnswer = new Button("Answer");
		Button cancelAnswer = new Button("Cancel");
		Button updateAnswer = new Button("Update");
		Button searchButton = new Button("Search");
	
		// HyperLinks
		Hyperlink reply = new Hyperlink("Reply");
		Hyperlink options = new Hyperlink("Options");
		Hyperlink markAsSolution = new Hyperlink("Mark as Solution");
		Hyperlink editQuestion = new Hyperlink("Edit");
		Hyperlink deleteQuestion = new Hyperlink("Delete");
		Hyperlink editAnswer = new Hyperlink("Edit");
		Hyperlink deleteAnswer = new Hyperlink("Delete");
		Hyperlink commitAnswer = new Hyperlink("Commit");
		Hyperlink followUp = new Hyperlink("Follow-Up");
		
		// Labels
		Label postError = new Label();
		postError.setVisible(false);
		
		// TextAreas
		TextArea postBody = new TextArea();
		postBody.setPromptText("Begin typing question details here");
		postBody.setWrapText(true);
		VBox.setVgrow(postBody, Priority.ALWAYS);
		HBox.setHgrow(postBody, Priority.ALWAYS);
		TextArea replyBody = new TextArea();
		replyBody.setPromptText("Begin typing answer here");
		replyBody.setWrapText(true);
		VBox.setVgrow(replyBody, Priority.ALWAYS);
		HBox.setHgrow(replyBody, Priority.ALWAYS);
		TextArea displayPostBody = new TextArea();
		displayPostBody.setWrapText(true);
		displayPostBody.setEditable(false);
		displayPostBody.setFont(Font.font("Comic Sans MS", 14));
		VBox.setVgrow(displayPostBody, Priority.ALWAYS);
		HBox.setHgrow(displayPostBody, Priority.ALWAYS);
		
		// Textfields
		TextField questionTitle = new TextField();
		questionTitle.setPromptText("Ask your question");
		questionTitle.setMaxWidth(Double.MAX_VALUE);
		TextField displayQuestionTitle = new TextField();
		displayQuestionTitle.setEditable(false);
		displayQuestionTitle.setFont(Font.font("Comic Sans MS", 14));
		displayQuestionTitle.setStyle("-fx-font-weight: bold;");
		displayQuestionTitle.setMaxWidth(Double.MAX_VALUE);
		
		// BorderPane
		BorderPane displayForumContainer = new BorderPane(); // container for all elements
		
		// HBoxes
		HBox optionsReplyContainer = new HBox();
		optionsReplyContainer.setStyle("-fx-alignment: center;");
		HBox editDeleteQuestionContainer = new HBox();
		editDeleteQuestionContainer.setStyle("-fx-alignment: center;");
		HBox editDeleteAnswer= new HBox();
		editDeleteAnswer.setStyle("-fx-alignment: center;");
		HBox postUpdateCancelQuestion = new HBox();
		postUpdateCancelQuestion.setStyle("-fx-alignment: center;");
		HBox postUpdateCancelAnswer = new HBox();
		postUpdateCancelAnswer.setStyle("-fx-alignment: center;");
		HBox backNew = new HBox();
		backNew.setStyle("-fx-alignment: center;");
		HBox mainDisplay = new HBox();
		mainDisplay.setStyle("-fx-alignment: center;");
		
		// VBoxes
		VBox displayWindowContainer = new VBox();
		displayWindowContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");
		VBox displayQuestionContainer = new VBox();
		displayQuestionContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");
		VBox newQuestionContainer = new VBox();
		newQuestionContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");
		VBox newAnswerContainer = new VBox();
		newAnswerContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");
		VBox displayAnswersWithButtons = new VBox();
		displayAnswersWithButtons.setStyle("-fx-alignment: center; -fx-padding: 10;");
		VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
		
	    // Spacers
	    Region buttonSpacer1 = new Region();
	    Region buttonSpacer2 = new Region();
	    Region buttonSpacer3 = new Region();
	    Region buttonSpacer4 = new Region();
	    Region buttonSpacer5 = new Region();
	    HBox.setHgrow(buttonSpacer1, Priority.ALWAYS);
	    HBox.setHgrow(buttonSpacer2, Priority.ALWAYS);
	    HBox.setHgrow(buttonSpacer3, Priority.ALWAYS);
	    HBox.setHgrow(buttonSpacer4, Priority.ALWAYS);
	    HBox.setHgrow(buttonSpacer5, Priority.ALWAYS);
		
	    // StackPanes
	    StackPane questionsArea = new StackPane();
	    StackPane answersArea = new StackPane();
	    StackPane displayQButtons = new StackPane();
		
		// Build the display question box
	    editDeleteQuestionContainer.getChildren().addAll(editQuestion, deleteQuestion, markAsSolution, followUp);
	    editDeleteQuestionContainer.setVisible(false);
	    editDeleteQuestionContainer.setManaged(false);
	    optionsReplyContainer.getChildren().addAll(options, editDeleteQuestionContainer, buttonSpacer1, reply);
	    options.setVisible(false);
	    //displayQButtons.getChildren().addAll(optionsReplyContainer, editDeleteQuestionContainer);
	    displayQuestionContainer.getChildren().addAll(displayQuestionTitle, displayPostBody, optionsReplyContainer);
	    displayQuestionContainer.setVisible(false);
	    
	    // Build the ask question box
	    postUpdateCancelQuestion.getChildren().addAll(postQuestion, updateQuestion, buttonSpacer2, postError, buttonSpacer3, cancelQuestion);
	    newQuestionContainer.getChildren().addAll(questionTitle, postBody, postUpdateCancelQuestion);
	    newQuestionContainer.setVisible(false);
	    newQuestionContainer.setManaged(false);
	    questionsArea.getChildren().addAll(displayQuestionContainer, newQuestionContainer);
	    questionsArea.setPrefHeight(450);
	    
	    // Build the new reply box
	    postUpdateCancelAnswer.getChildren().addAll(postAnswer, updateAnswer, commitAnswer, buttonSpacer4, cancelAnswer);
	    newAnswerContainer.getChildren().addAll(replyBody, postUpdateCancelAnswer);
	    newAnswerContainer.setVisible(false);
	    
	    // Build ListView to display question titles
		try {questions = databaseHelper.getQuestions();} catch (SQLException e) {e.printStackTrace();}
	    ObservableList<Question> observableQuestions = FXCollections.observableArrayList(questions);
	    ListView<Question> qListView = new ListView<>();
	    qListView.setItems(observableQuestions);
		VBox.setVgrow(qListView, Priority.ALWAYS);
	    
	    // Question ListView settings (added showing badge for resolution and follow-up)
		qListView.setCellFactory(param -> new ListCell<Question>() {
	        @Override
	        protected void updateItem(Question question, boolean empty) {
	            super.updateItem(question, empty);

	            if (empty || question == null) {
	                setText(null);
	                setGraphic(null);
	                setBorder(null);
	                setStyle("");
	            } else {
	                Label titleLabel = new Label(question.getTitle());
	                titleLabel.setFont(Font.font("Comic Sans MS", 13));
	                
	                // Badges
	                Label resolvedBadge = new Label("Resolved");
	                resolvedBadge.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 2 6 2 6; -fx-background-radius: 6;");
	                resolvedBadge.setVisible(question.isResolved());
	                resolvedBadge.setManaged(question.isResolved());
	                
	                Label followUpBadge = new Label("Follow-Up");
	                followUpBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 2 6 2 6; -fx-background-radius: 6;");
	                followUpBadge.setVisible(question.hasFollowUp());
	                followUpBadge.setManaged(question.hasFollowUp());
	                
	                HBox cellContent = new HBox(8, titleLabel, resolvedBadge, followUpBadge);
	                cellContent.setAlignment(Pos.CENTER_LEFT);
	                cellContent.setPrefWidth(0);
	                
	                // If resolved, tint the background lightly green for the whole cell
	                if (question.isResolved()) {
	                    setStyle("-fx-background-color: #eaffea; -fx-text-fill: black;");
	                } else {
	                    setStyle("-fx-background-color: white; -fx-text-fill: black;");
	                }

	                setText(null);
	                setGraphic(cellContent);

	                if (isSelected()) {
	                    // Applies a border when selected
	                    setBorder(new Border(new BorderStroke(
	                            Color.GRAY,
	                            BorderStrokeStyle.SOLID,
	                            CornerRadii.EMPTY,
	                            new BorderWidths(2)
	                    )));
	                } else {
	                    setBorder(null);
	                }
	            }
	        }
	    });
	    
	    // Answer ListView settings
		aListView.setCellFactory(param -> new ListCell<Answer>() {
	        @Override
	        protected void updateItem(Answer answer, boolean empty) {
	            super.updateItem(answer, empty);

	            if (empty || answer == null) {
	                setText(null);
	                setGraphic(null);
	                setStyle("");
	            } else {
	            	String body = answer.getBody();
	            	boolean solution = answer.isSolution();
	            	if (solution) {
	            		// Highlight solution with yellow background and a checkmark
	            		Text check = new Text("✔ ");
	            		check.setFill(Color.web("#b8860b")); // dark gold
	            		check.setFont(Font.font("Comic Sans MS", 12));
	            		Text bodyText = new Text(body);
	            		bodyText.setFont(Font.font("Comic Sans MS", 12));
	            		bodyText.setFill(Color.BLACK);
	            		TextFlow tf = new TextFlow(check, bodyText);
	            		setGraphic(tf);
	            		setText(null);
	            		setStyle("-fx-background-color: #fff7cc;"); // light yellow
	            	} else if(body.contains("@@@")) {
		                String[] parts = body.split("@@@", 2);
		                String grayText = parts[0].trim();
		                String blackText = parts[1].trim();
		                Text grayPart = new Text("~"+grayText+"~"+ "\n");
		                grayPart.setFill(Color.GRAY);
		                Text blackPart = new Text("        "+blackText);
		                blackPart.setFill(Color.BLACK);
		                TextFlow textFlow = new TextFlow(grayPart, blackPart);
		                setGraphic(textFlow);
		                setText(null);
		                setStyle("-fx-background-color: white;");
	            	} else {
	                    setText(answer.getBody());
	                    setGraphic(null);
	                    setStyle("-fx-background-color: white;");
	            	}
	                
	                setFont(Font.font("Comic Sans MS", 12));
	                setWrapText(true);
	                setPrefWidth(0);
	                
	                if (isSelected()) {
	                    // Applies a border when selected
	                    setBorder(new Border(new BorderStroke(
	                            Color.GRAY,
	                            BorderStrokeStyle.SOLID,
	                            CornerRadii.EMPTY,
	                            new BorderWidths(2)
	                    )));
	                } else {
	                    setBorder(null);
	                }
	            }
	        }
	    });
	    
		editDeleteAnswer.getChildren().addAll(editAnswer, buttonSpacer1, commitAnswer, buttonSpacer2, deleteAnswer);
	    editDeleteAnswer.setVisible(false);
	    displayAnswersWithButtons.getChildren().addAll(aListView, editDeleteAnswer);
	    answersArea.getChildren().addAll(newAnswerContainer, displayAnswersWithButtons);
	    aListView.setVisible(false);
	    
	    // commit Answer function
		commitAnswer.setOnAction(event -> {
			String originalText = aListView.getSelectionModel().getSelectedItem().getBody();
			replyBody.setText(originalText);
			replyBody.setStyle("-fx-text-fill: gray;");
			
			replyBody.textProperty().addListener((obs, oldText, newText) -> {
			    if (!newText.equals(originalText)) {
			        replyBody.setStyle("-fx-text-fill: black;");
			    }
			});

			newAnswerContainer.setVisible(true);
			newAnswerContainer.setManaged(true);

			displayAnswersWithButtons.setVisible(false);
			displayAnswersWithButtons.setManaged(false);

			postAnswer.setVisible(false);
			postAnswer.setManaged(false);

			updateAnswer.setVisible(false);
			updateAnswer.setManaged(false);


			Button confirmCommit = new Button("Confirm Commit");
			postUpdateCancelAnswer.getChildren().add(confirmCommit);

			confirmCommit.setOnAction(subEvent -> {
		        String wholeBody = replyBody.getText();
		        String newSuffix = wholeBody.substring(originalText.length());

		        String combinedText = originalText.trim() + "@@@" + newSuffix;

		        Answer answer = new Answer(aId, currentUser.getUserName(), combinedText);
				try {
					databaseHelper.addAnswer(answer, qId);
					ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
					aListView.setItems(updater);
					replyBody.clear();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				postUpdateCancelAnswer.getChildren().remove(confirmCommit);
				newAnswerContainer.setVisible(false);
				newAnswerContainer.setManaged(false);
				displayAnswersWithButtons.setVisible(true);
				displayAnswersWithButtons.setManaged(true);
			});
		});

		
	    TextField searchBox = new TextField();
	    searchBox.setPromptText("Search questions...");
	    ListView<String> searchResultsView = new ListView<>();
	    searchResultsView.setVisible(false);
	    searchResultsView.setPrefWidth(600);
	    VBox.setVgrow(searchResultsView, Priority.ALWAYS);
	    HBox.setHgrow(searchResultsView, Priority.ALWAYS);
	    questionsArea.getChildren().add(searchResultsView);
		
	    // Build the main display
	    VBox.setVgrow(questionsArea, Priority.ALWAYS);
	    VBox.setVgrow(answersArea, Priority.ALWAYS);
	    
	    displayWindowContainer.getChildren().addAll(questionsArea, answersArea);
	    backNew.getChildren().addAll(back, newQuestion,buttonSpacer1, searchBox, searchButton);
	    mainDisplay.getChildren().addAll(qListView, displayWindowContainer);
	    displayForumContainer.setCenter(mainDisplay);
	    displayForumContainer.setBottom(backNew);
	    BorderPane.setAlignment(backNew, Pos.BOTTOM_LEFT);
	    
	    
	    Runnable hideSearchResults = () -> {
	        searchResultsView.setVisible(false);
	        searchResultsView.setManaged(false);
	        qListView.setVisible(true);
	        qListView.setManaged(true);
	    };
	    
	    // Button and HyperLink actions
	    back.setOnAction(event -> {
	    	hideSearchResults.run(); 
	        switch(userRole) {
	        case "Admin":
	        	new AdminHomePage(databaseHelper, currentUser).show(primaryStage);
	        	break;
	        case "Student":
	        	new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
	        	break;
	        case "Reviewer":
	        	new ReviewerHomePage(databaseHelper).show(primaryStage);
	        	break;
	        case "Instructor":
	        	new InstructorHomePage(databaseHelper).show(primaryStage);
	        	break;
	        case "Staff":
	        	new StaffHomePage(databaseHelper).show(primaryStage);
	        	break;
	        default:
	        	System.out.println("Error: Missing userRole");
	        }
	    });
	    
	    // Display selected post
	    qListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            	this.qId = newValue.getID();
    	    	editDeleteQuestionContainer.setVisible(false);
    	    	editDeleteQuestionContainer.setManaged(false);
            	displayQuestionTitle.setText(newValue.getTitle());
	        	displayPostBody.setText(newValue.getBody());
		    	optionsReplyContainer.setVisible(true);
		    	answersArea.setVisible(true);
	        	if(newValue.getPoster().equals(currentUser.getUserName())) {
	        		options.setVisible(true);
	        		options.setManaged(true);
	        	}
	        	
	        	// Display associated answers
	        	try {
	        		ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
		        	aListView.setItems(updater);
		        	aListView.setVisible(true);
		        	aListView.setManaged(true);
		        	newAnswerContainer.setManaged(false);
		        	newAnswerContainer.setVisible(false);
	        	} catch (SQLException e) {e.printStackTrace();}
	        	
	        	displayQuestionContainer.setVisible(true);
            }
        });
	    
	    // Display option to edit or delete selected answer
	    aListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            	this.aId = newValue.getID();
		    	if(newValue.getPoster().equals(currentUser.getUserName())) {
	    	    	replyBody.setText(newValue.getBody());
	                editDeleteAnswer.setVisible(true);
	        	}
            } else {
                editDeleteAnswer.setVisible(false);
            }
        });
	    
	    qListView.setOnMouseClicked(event -> {
	    	if (event.getTarget() instanceof ListView) {
	    		qListView.getSelectionModel().clearSelection();
	        }
	    });
	    
	    aListView.setOnMouseClicked(event -> {
	    	if (event.getTarget() instanceof ListView) {
	    		aListView.getSelectionModel().clearSelection();
	        }
	    });
	    
	    newQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
    		qListView.getSelectionModel().clearSelection();
	    	newQuestionContainer.setVisible(true);
	    	newQuestionContainer.setManaged(true);
	    	displayQuestionContainer.setVisible(false);
	    	displayQuestionContainer.setManaged(false);
	    	updateQuestion.setVisible(false);
	    	updateQuestion.setManaged(false);
	    	answersArea.setVisible(false);
	    	answersArea.setManaged(false);
	    });
	   
	    // Implement marking an answer as the solution for the selected question
	    markAsSolution.setOnAction(event -> {
	    	hideSearchResults.run();
	    	Answer selected = aListView.getSelectionModel().getSelectedItem();
	    	if (selected == null) {
	    		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select an answer to mark as the solution.");
	    		alert.showAndWait();
	    		return;
	    	}
	    	try {
	    		databaseHelper.markAnswerAsSolution(qId, selected.getID());
	    		// refresh answers and questions
	    		ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
	        	aListView.setItems(updater);
	        	ObservableList<Question> qUpdater = FXCollections.observableArrayList(databaseHelper.getQuestions());
	        	qListView.setItems(qUpdater);
	        	Alert done = new Alert(Alert.AlertType.INFORMATION, "Marked selected answer as solution and question as resolved.");
	        	done.showAndWait();
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    		Alert err = new Alert(Alert.AlertType.ERROR, "Failed to mark answer as solution.");
	    		err.showAndWait();
	    	}
	    });
	    
	    // Implement follow-up creation (available to the question poster)
	    followUp.setOnAction(event -> {
	    	hideSearchResults.run();
	    	// Ensure a question is selected
	    	if (qId == 0) {
	    		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a question first.");
	    		alert.showAndWait();
	    		return;
	    	}
	    	// Simple follow-up dialog stage
	    	Stage followUpStage = new Stage();
	    	VBox fuLayout = new VBox(8);
	    	fuLayout.setStyle("-fx-padding: 10;");
	    	TextField fuTitle = new TextField();
	    	fuTitle.setPromptText("Follow-up title");
	    	TextArea fuBody = new TextArea();
	    	fuBody.setPromptText("Follow-up body");
	    	fuBody.setPrefRowCount(6);
	    	Button postFU = new Button("Post Follow-Up");
	    	Button cancelFU = new Button("Cancel");
	    	Label fuError = new Label();
	    	fuError.setStyle("-fx-text-fill: red;");
	    	HBox buttons = new HBox(8, postFU, cancelFU);
	    	fuLayout.getChildren().addAll(new Label("Create Follow-Up"), fuTitle, fuBody, buttons, fuError);
	    	Scene fuScene = new Scene(fuLayout, 400, 300);
	    	followUpStage.setScene(fuScene);
	    	followUpStage.setTitle("Follow-Up");
	    	followUpStage.show();
	    	
	    	postFU.setOnAction(a -> {
	    		if (fuTitle.getText().trim().isEmpty() || fuBody.getText().trim().isEmpty()) {
	    			fuError.setText("Title and body are required.");
	    			return;
	    		}
	    		try {
	    			databaseHelper.addFollowUpQuestion(qId, currentUser.getUserName(), fuTitle.getText(), fuBody.getText());
	    			// refresh question list
	    			ObservableList<Question> qUpdater = FXCollections.observableArrayList(databaseHelper.getQuestions());
		        	qListView.setItems(qUpdater);
		        	followUpStage.close();
		        	Alert done = new Alert(Alert.AlertType.INFORMATION, "Follow-up posted.");
		        	done.showAndWait();
	    		} catch (Exception ex) {
	    			ex.printStackTrace();
	    			fuError.setText("Failed to post follow-up.");
	    		}
	    	});
	    	cancelFU.setOnAction(a -> followUpStage.close());
	    });
	    
	    searchButton.setOnAction(event -> {
	        try {
	            String text = searchBox.getText().trim().toLowerCase();
	            List<Question> allQuestions = databaseHelper.getQuestions();
	            List<Answer> allAnswers = databaseHelper.getAnswers();

	            List<String> finalDisplay = new ArrayList<>();

	            for (Question q : allQuestions) {
	                if (q.getTitle().toLowerCase().contains(text) || q.getBody().toLowerCase().contains(text)) {
	                    finalDisplay.add("[Question] " + q.getTitle() + " — " + q.getBody());
	                }
	            }

	            for (Answer a : allAnswers) {
	                if (a.getBody().toLowerCase().contains(text)) {
	                    finalDisplay.add("[Answer] " + a.getBody());
	                }
	            }

	            ObservableList<String> searchResults = FXCollections.observableArrayList(finalDisplay);
	            searchResultsView.setItems(searchResults);


	            newQuestionContainer.setVisible(false);
	            newQuestionContainer.setManaged(false);
	            displayQuestionContainer.setVisible(false);
	            displayQuestionContainer.setManaged(false);
	            answersArea.setVisible(false);
	            answersArea.setManaged(false);
	            qListView.setVisible(true);
	            qListView.setManaged(true);
	            searchResultsView.setVisible(true);
	            searchResultsView.setManaged(true);

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    });
	    
	    postQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
	    	if(questionTitle.getText().isEmpty()) {
	    		postError.setText("Error, missing title");
	    		postError.setVisible(true);
	    	}
	    	else if(postBody.getText().isEmpty()) {
	    		postError.setText("Error, missing body");
	    		postError.setVisible(true);
	    	}
	    	else {
		    	Question question = new Question(currentUser.getUserName(), questionTitle.getText(), postBody.getText());
		    	try {
					databaseHelper.addQuestion(question);
			    	observableQuestions.add(question);
			    	
			    	questionTitle.clear();
			    	postBody.clear();
			    	
			    	newQuestionContainer.setVisible(false);
			    	newQuestionContainer.setManaged(false);
				    displayQuestionContainer.setManaged(true);
			    	updateQuestion.setVisible(true);
			    	updateQuestion.setManaged(true);
			    	answersArea.setManaged(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
	    });
	    
	    reply.setOnAction(event -> {
	    	hideSearchResults.run();
	    	replyBody.clear();
	    	newAnswerContainer.setVisible(true);
	    	newAnswerContainer.setManaged(true);
	    	updateAnswer.setVisible(false);
	    	updateAnswer.setManaged(false);
	    	postAnswer.setVisible(true);
	    	postAnswer.setManaged(true);
	    	displayAnswersWithButtons.setVisible(false);
	    	displayAnswersWithButtons.setManaged(false);
	    });
	    
	    postAnswer.setOnAction(event -> {
	    	hideSearchResults.run();
	    	Answer answer = new Answer(qId, currentUser.getUserName(), replyBody.getText());
	    	try {
				databaseHelper.addAnswer(answer, qId);
        		ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
	        	aListView.setItems(updater);
		    	replyBody.clear();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	newAnswerContainer.setVisible(false);
	    	newAnswerContainer.setManaged(false);
	    	updateAnswer.setVisible(true);
	    	updateAnswer.setManaged(true);
	    	displayAnswersWithButtons.setVisible(true);
	    	displayAnswersWithButtons.setManaged(true);
	    });
	    
	    cancelAnswer.setOnAction(event -> {
	    	hideSearchResults.run();
	    	replyBody.clear();
	    	newAnswerContainer.setVisible(false);
	    	newAnswerContainer.setManaged(false);
	    	updateAnswer.setVisible(true);
	    	updateAnswer.setManaged(true);
	    	displayAnswersWithButtons.setVisible(true);
	    	displayAnswersWithButtons.setManaged(true);
	    });
	    
	    cancelQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
	    	questionTitle.clear();
	    	postBody.clear();
	    	newQuestionContainer.setVisible(false);
	    	newQuestionContainer.setManaged(false);
	    	displayQuestionContainer.setManaged(true);
	    	updateQuestion.setVisible(true);
	    	updateQuestion.setManaged(true);
	    	answersArea.setManaged(true);
	    });
	    
	    options.setOnAction(event -> {
	    	hideSearchResults.run();
	    	editDeleteQuestionContainer.setVisible(true);
	    	editDeleteQuestionContainer.setManaged(true);
	    	options.setVisible(false);
	    	options.setManaged(false);
	    });
	    
	    editQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
	    	questionTitle.setText(displayQuestionTitle.getText());
	    	postBody.setText(displayPostBody.getText());
	    	
	    	newQuestionContainer.setVisible(true);
	    	newQuestionContainer.setManaged(true);
	    	displayQuestionContainer.setVisible(false);
	    	displayQuestionContainer.setManaged(false);
	    	postQuestion.setVisible(false);
	    	postQuestion.setManaged(false);
	    });
	    
	    editAnswer.setOnAction(event -> {
	    	hideSearchResults.run();
		    newAnswerContainer.setVisible(true);
		    newAnswerContainer.setManaged(true);
		    displayAnswersWithButtons.setVisible(false);
		    displayAnswersWithButtons.setManaged(false);
			postAnswer.setVisible(false);
			postAnswer.setManaged(false);
	    });
	    
	    deleteQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
	    	try {
				databaseHelper.deleteQuestion(qId);
				ObservableList<Question> updater = FXCollections.observableArrayList(databaseHelper.getQuestions());
				qListView.setItems(updater);
				displayAnswersWithButtons.setVisible(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    });
	    
	    deleteAnswer.setOnAction(event -> {
	    	hideSearchResults.run();
	    	try {
				databaseHelper.deleteAnswer(aId);
				ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
				aListView.setItems(updater);
				replyBody.clear();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    });
	    
	    updateQuestion.setOnAction(event -> {
	    	hideSearchResults.run();
	    	displayQuestionTitle.setText(questionTitle.getText());
	    	displayPostBody.setText(postBody.getText());
	    	try {
				databaseHelper.editQTitle(qId, questionTitle.getText());
				databaseHelper.editQBody(qId, postBody.getText());
				questionTitle.clear();
				postBody.clear();
				ObservableList<Question> updater = FXCollections.observableArrayList(databaseHelper.getQuestions());
				qListView.setItems(updater);
				
				newQuestionContainer.setVisible(false);
		    	newQuestionContainer.setManaged(false);
		    	displayQuestionContainer.setVisible(true);
		    	displayQuestionContainer.setManaged(true);
		    	postQuestion.setVisible(true);
		    	postQuestion.setManaged(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    });
	    
	    updateAnswer.setOnAction(event -> {
	    	hideSearchResults.run();
	    	try {
				databaseHelper.editABody(aId, replyBody.getText());
				ObservableList<Answer> updater = FXCollections.observableArrayList(databaseHelper.getAnswers(qId));
				aListView.setItems(updater);
				
				displayAnswersWithButtons.setVisible(true);
				displayAnswersWithButtons.setManaged(true);
				postAnswer.setVisible(true);
				postAnswer.setManaged(true);
			    newAnswerContainer.setVisible(false);
			    newAnswerContainer.setManaged(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    });
	    
	    //layout.getChildren().addAll(displayForumContainer);
	    Scene userScene = new Scene(displayForumContainer, 800, 600);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Forum Page");
	}

}