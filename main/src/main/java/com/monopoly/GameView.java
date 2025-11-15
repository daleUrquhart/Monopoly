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
     * Jail Pane holding the image of jail abrs for user to interact with on a jail turn
     */
    private GridPane jailPane;

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
     * Assigns the jailPane to Gameview
     * @param centerPane
     */
    void setJailPane(GridPane jailPane) {
        this.jailPane = jailPane;
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
     * Adds dice to the center
     */
    void showDice() {
        centerPane.getChildren().add(dicePane); 
    }

    /**
     * Gets the GridPane holding the dice
     * @return the GridPane holding the dice
     */
    GridPane getDicePane() {
        return dicePane;
    }

    /**
     * Hides the dice pane from view
     */
    private void hideDice() {
        centerPane.getChildren().remove(dicePane);
    }

    /**
     * hides jailBars
     */
    private void hideJail() {
        centerPane.getChildren().remove(jailPane);
    }

    /**
     * Hides whatever prompt is on the centerPane
     */
    void hidePrompt() {
        if(centerPane.getChildren().contains(getDicePane())) hideDice();
        else if(centerPane.getChildren().contains(getJailPane())) hideJail();
    } 

    void deletePrompts() {
        jailPane = null;
        dicePane = null;
    }

    /**
     * Shows jail bars on the center
     */
    void showJail() {
        centerPane.getChildren().add(jailPane);
    }

    /**
     * Returns the jailPane instance
     */
    GridPane getJailPane() {
        return jailPane;
    }
}
