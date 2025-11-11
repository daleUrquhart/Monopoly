package com.monopoly;

import java.util.List;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameView {

    /**
     * Primary pane
     */
    private final GridPane mainPane;

    /**
     * Dice Pane holding only the two dice
     */
    private GridPane dicePane;

    /**
     * Display pane (Space right of the board)
     */
    private final MessagePane messagePane;

    /**
     * StackPane Center pane of the board holding dice or jail info
     */
    private StackPane centerPane;

    /**
     * Sets up initial layout of the board 
     */
    GameView() {
        mainPane = new GridPane(); 
        messagePane = new MessagePane();
        
        mainPane.add(messagePane, 11, 0, GridPane.REMAINING, GridPane.REMAINING);  
    } 

    /**
     * Assigns the dice pane to the GameView
     * @param dicePane GridPane holding the pair of dice
     */
    void setDicePane(GridPane dicePane) {
        this.dicePane = dicePane;  
    }

    /**
     * Assigns teh center pane fo the board
     * @param center center pane of the board
     */
    void setCenterPane(StackPane centerPane) {
        try{
            this.centerPane = centerPane;
        } catch(Exception e) {
            System.err.println(e);
        } 
    }
    
    /**
     * Gets the GridPane holding the dice
     * @return the GridPane holding the dice
     */
    GridPane getDicePane() {
        return dicePane;
    }

    /**
     * Gets the Display Pane mainting all data to the right of the board
     * @return primary display pane
     */
    MessagePane getMessagePane() {
        return messagePane;
    }
    
    /**
     * Gets the JavaFX Scene
     * @return The JavaFX Scene
     */
    Scene getScene() {
        return new Scene(mainPane, 800, 800);
    }

    /**
     * Highest level GUI component
     * @return Maine GridPane Board is built on 
     */
    GridPane getMainPane() {
        return mainPane;
    }

    /**
     * Displays an error dialog with a given title and message.
     * @param title Title for the error dialog
     * @param message Message for the error dialog
     */
    static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 
    /**
     * Raises a dialog to input an integer and validates it against a given range.
     * @param title Title for the input dialog
     * @param header Header for the input dialog
     * @param context Context for the input dialog
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return The valid integer input from the user
     */
    static int getIntInput(String title, String header, String context, int min, int max) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(title);
            dialog.setHeaderText(header);
            dialog.setContentText(context + " (Range: " + min + " to " + max + ")");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    int value = Integer.parseInt(result.get());
                    if (value >= min && value <= max) {
                        return value;
                    } else {
                        showError("Invalid Input", "Value must be between " + min + " and " + max + ".");
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter a valid integer.");
                }
            } else {
                // User cancelled the input
                throw new RuntimeException("Input was cancelled");
            }
        }
    }

    /**
     * Raises an alert and returns its input
     * @param title Title for alert raised
     * @param header Header for alert raised
     * @param context Context for alert raised
     * @return true for if teh response is 'yes'
     */
    static boolean getBoolInput(String title, String header, String context) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        
        // Show and wait for response
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        
        // Return true if 'OK' is clicked, false otherwise
        return result == ButtonType.OK;
    } 

    /**
     * Shows an alert
     * @param title Title of the alert
     * @param message Message of the alert
     */ 
    static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a dialog for selecting an item from a list of choices.
     * @param <T> The type of the items in the list
     * @param title The title of the dialog
     * @param header The header text for the dialog
     * @param content The content text for the dialog
     * @param choices The list of available choices
     * @param defaultChoice The default choice (can be null)
     * @return The selected item, or null if no selection was made
     */
    <T> T showDialog(String title, String header, String content, List<T> choices, T defaultChoice) {
        // Create a ChoiceDialog with the provided choices and default selection
        ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        // Show the dialog and return the result
        Optional<T> result = dialog.showAndWait();
        return result.orElse(null); // Return null if no selection was made
    }

    /**
     * Removes die form center pane
     */
    void removeDice() {
        centerPane.getChildren().remove(dicePane); 
    }

    /**
     * Adds dice to the center
     */
    void showDice() {
        try {
            centerPane.getChildren().add(dicePane);
        } catch (Exception e) {
            System.err.println(e);
        } 
    }
}
