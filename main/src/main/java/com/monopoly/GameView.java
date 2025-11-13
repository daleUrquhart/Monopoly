package com.monopoly;

import javafx.scene.Scene;
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
            System.out.println(e);
        } 
    }
}
