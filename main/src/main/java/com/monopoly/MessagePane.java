package com.monopoly;

import java.util.List;
import java.util.function.Consumer;

import com.monopoly.boardspaces.Property;

import javafx.geometry.Pos; 
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * An API of tools for working with the message board
 * Seperates message logic from GameView
 */
public class MessagePane extends GridPane { 

    /**
     * VBox Current Player display holding information pertaining to player state
     */
    private final VBox currentPlayerDisplay;

    /**
     * VBox Message display holding information pertaining to turn state
     */
    private final VBox messageDisplay;


    MessagePane() { 
        currentPlayerDisplay = new VBox();
        messageDisplay = new VBox();

        add(messageDisplay, 0, 0); 
        add(currentPlayerDisplay, 0, 1); 
    } 

    void addDisplays() {
        add(messageDisplay, 0, 0); 
        add(currentPlayerDisplay, 0, 1);
    }

    void clear() {
        clearMessages();
        clearCurrentPlayerDisplay();
    }

    /**
     * Clears Current player display
     */
    void clearCurrentPlayerDisplay() {
        currentPlayerDisplay.getChildren().clear();
    } 

    /**
     * Clears turn related messages form messagePane
     */
    public void clearMessages() {
        messageDisplay.getChildren().clear();
    } 

    /**
     * Adds a message to the top of the message board
     * @param message Message to be displayed
     */
    public void showMessage(String message) {  
        Label label = new Label(message);
        label.setWrapText(true);
        messageDisplay.getChildren().add(label);
    }  
    
    /**
     * Displays a button with a message, Runnable executed on click
     */
    public void showAck(String message, Runnable onClick) {
        Label label = new Label(message);
        label.setWrapText(true);

        Button ack = new Button("OK");

        VBox container = new VBox(10, label, ack);
        container.setAlignment(Pos.CENTER_LEFT);

        ack.setOnAction(e -> {
            messageDisplay.getChildren().remove(container);
            onClick.run();
        });

        messageDisplay.getChildren().add(container);
    }


    /**
     * Displays a yes/no question inline using radio buttons instead of a popup.
     * Blocks further input until submitted, then calls the provided callback.
     *
     * @param pane GridPane to attach to
     * @param title Title/label for the prompt
     * @param header Optional header or question text
     * @param context Context text explaining the choice
     * @param callback Code to execute with the result (true for yes, false for no)
     */
    void getIntInput(String title, String header, String context, int min, int max, Consumer<Integer> callback) {
        // Title + context display
        Label question = new Label(title + "\n" + header + "\n" + context);
        question.setWrapText(true);

        // Text field and submit button
        TextField tf = new TextField();
        tf.setPromptText("Enter a number between " + min + " and " + max);

        Button submit = new Button("Submit");

        // Horizontal layout for field + button
        HBox inputBox = new HBox(10, tf, submit);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        // Vertical layout container
        VBox container = new VBox(10, question, inputBox);
        container.setAlignment(Pos.CENTER_LEFT);

        // Add to pane (GridPane child container)
        messageDisplay.getChildren().add(container);

        // Handle user submission
        submit.setOnAction(e -> {
            try {
                int value = Integer.parseInt(tf.getText().trim());

                if (value < min || value > max) {
                    question.setText(
                        "Value must be between " + min + " and " + max + ". Try again."
                    );
                    return;
                }

                // Clean up the UI
                messageDisplay.getChildren().remove(container);

                // Send result to the callback
                callback.accept(value);

            } catch (NumberFormatException ex) {
                question.setText("Please enter a valid integer between " + min + " and " + max + ".");
            } catch (Exception ex) {
                question.setText("Unexpected error: " + ex.getMessage());
            }
        });
    }


    /**
     * Displays a yes/no question inline using radio buttons instead of a popup.
     * Blocks further input until submitted, then calls the provided callback.
     *
     * @param pane GridPane to attach to
     * @param title Title/label for the prompt
     * @param header Optional header or question text
     * @param context Context text explaining the choice
     * @param callback Code to execute with the result (true for yes, false for no)
     */
    void getBoolInput(String context, Consumer<Boolean> callback) {
        // Create question label
        Label question = new Label(context);
        question.setWrapText(true);

        // Create radio buttons
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        ToggleGroup toggle = new ToggleGroup();
        yes.setToggleGroup(toggle);
        no.setToggleGroup(toggle);

        // Create submit button
        Button submit = new Button("Submit");

        // Layout in an HBox
        HBox options = new HBox(10, yes, no, submit);
        VBox container = new VBox(10, question, options);

        // Add to pane (adjust grid position as needed)
        messageDisplay.getChildren().add(container);

        // Handle submission
        submit.setOnAction(e -> {
            Toggle selected = toggle.getSelectedToggle();
            if (selected == null) {
                question.setText("Please select an option before submitting.");
                return;
            }

            boolean result = (selected == yes);
            messageDisplay.getChildren().remove(container);
            callback.accept(result);
        });
    }

    void getChoiceInput(String context, List<String> options, Consumer<String> callback) {
        Label question = new Label(context);
        question.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, question, buttonBox);
        container.setAlignment(Pos.CENTER_LEFT);
        messageDisplay.getChildren().add(container);

        // Create buttons for each available option
        for (String option : options) {
            Button button = new Button(option);
            button.setOnAction(e -> {
                messageDisplay.getChildren().remove(container);
                callback.accept(option);
            });
            buttonBox.getChildren().add(button);
        }
    }

    /**
     * Displays the information on DispPane of main player below any messages pertaining to current turn
     * @param p Current player
     */
    void displayCurrent(Player current, GameController controller) { 
        clearCurrentPlayerDisplay();
        Label data = new Label(current.getName() + "'s turn\nBalance:" + current.getBalance() + "\n"); 
        HBox pBox;
        Button buyDevelopmentBoxB, mortgageB, unMortgageB, auctionB, privateSaleB, sellB, sellDevelopmentB; 

        currentPlayerDisplay.getChildren().addAll(data);
        //display.getChildren().addAll(current.getPiece(), data); Try not adding piece to  view to see if it stays on board

        // Lengthy button display logic (dont offer to mortage an already mortgaged property, etc.)
        for(Property p : current.getProperties()) { 
            // Property HBox to be added to player
            pBox = new HBox();
            pBox.getChildren().add(new Label(p.toString())); 

            // lengthy check for buying development
            if(current.ownsSetFor(p) && current.canAfford(p.getDevelopmentCost()) && !p.hasHotel() && !p.isMortgaged()) {
                buyDevelopmentBoxB = new Button("Buy Development");
                buyDevelopmentBoxB.setOnMouseClicked(e -> {p.buyDevelopment(); displayCurrent(current, controller);});
                pBox.getChildren().add(buyDevelopmentBoxB);
            } 
 
            if(p.isMortgaged()) {                                     // Mortgaged, offer to unmortgage it
                unMortgageB = new Button("Un Mortgage");
                unMortgageB.setOnMouseClicked(e -> {p.unMortgage(); displayCurrent(current, controller);});
                pBox.getChildren().add(unMortgageB);
            } else if(!p.developed()){                                // Not mortgaged, if no development offer to mortgage
                mortgageB = new Button("Mortgage property");
                mortgageB.setOnMouseClicked(e -> {p.mortgage(); displayCurrent(current, controller);});
                pBox.getChildren().add(mortgageB);
            } else {                                                  // Not mortgaged, has developemtn, offer to sell one
                sellDevelopmentB = new Button("Sell development");
                sellDevelopmentB.setOnMouseClicked(e -> {p.sellDevelopment(); displayCurrent(current, controller);});

                

                pBox.getChildren().addAll(sellDevelopmentB); 
            }

            // Cant have developments, can be mortgaged
            if(!p.developed()) {
                sellB = new Button("Sell to Bank");
                sellB.setOnMouseClicked(e -> {current.sell(p); displayCurrent(current, controller);}); 
                
                // The next two are handled with controller as it requires additional inputs
                auctionB = new Button("Auction");
                auctionB.setOnMouseClicked(e -> {controller.handleAuction(p); displayCurrent(current, controller);}); 

                privateSaleB = new Button("Private Sale");
                privateSaleB.setOnMouseClicked(e -> {controller.handlePrivateSale(p); displayCurrent(current, controller);});
                pBox.getChildren().addAll(sellB, auctionB, privateSaleB);
            }  

            currentPlayerDisplay.getChildren().add(pBox);
        }  
    } 
}
