package com.monopoly;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        this.add(messageDisplay, 0, 0); 
        this.add(currentPlayerDisplay, 0, 1); 
    }

    /**
     * Clears turn related messages form messagePane
     */
    void clearMessages() {
        messageDisplay.getChildren().clear();
    }

    /**
     * Clears all text from message board
     * Empties the messagePane and currentPlayerDisplay 
     
    void clearDispPane() { 
        messageDisplay.getChildren().clear();
        currentPlayerDisplay.getChildren().clear();
    } 
    */

    /**
     * Displays the information on DispPane of main player below any messages pertaining to current turn
     * @param p Current player
     */
    void displayCurrent(Player current, GameController controller) { 
        updateCurrentPlayerDislay(current, controller);
    }

    void addDisplays() {
        this.add(messageDisplay, 0, 0); 
        this.add(currentPlayerDisplay, 0, 1);
    }

    /**
     * Clears Current player display
     */
    void clearCurrentPlayerDisplay() {
        currentPlayerDisplay.getChildren().clear();
    }

    /**
     * Adds a message to the top of the message board
     * @param message Message to be displayed
     */
    void showMessage(String message) {  
        messageDisplay.getChildren().add(new Label(message));  
    } 
    
    /**
     * Gets the GUI Player display in a VBox
     */
    void updateCurrentPlayerDislay(Player current, GameController controller) {
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
                buyDevelopmentBoxB.setOnMouseClicked(e -> p.buyDevelopment());
                pBox.getChildren().add(buyDevelopmentBoxB);
            } 
 
            if(p.isMortgaged()) {                                     // Mortgaged, offer to unmortgage it
                unMortgageB = new Button("Un Mortgage");
                unMortgageB.setOnMouseClicked(e -> p.unMortgage());
                pBox.getChildren().add(unMortgageB);
            } else if(!p.developed()){                                // Not mortgaged, if no development offer to mortgage
                mortgageB = new Button("Mortgage property");
                mortgageB.setOnMouseClicked(e -> p.mortgage());
                pBox.getChildren().add(mortgageB);
            } else {                                                  // Not mortgaged, has developemtn, offer to sell one
                sellDevelopmentB = new Button("Sell development");
                sellDevelopmentB.setOnMouseClicked(e -> p.sellDevelopment());

                

                pBox.getChildren().addAll(sellDevelopmentB); 
            }

            // Cant have developments, can be mortgaged
            if(!p.developed()) {
                sellB = new Button("Sell to Bank");
                sellB.setOnMouseClicked(e -> current.sell(p)); 
                
                // The next two are handled with controller as it requires additional inputs
                auctionB = new Button("Auction");
                auctionB.setOnMouseClicked(e -> controller.handleAuction(p)); 

                privateSaleB = new Button("Private Sale");
                privateSaleB.setOnMouseClicked(e -> controller.handlePrivateSale(p));
                pBox.getChildren().addAll(sellB, auctionB, privateSaleB);
            }  

            currentPlayerDisplay.getChildren().add(pBox);
        }  
    } 
}
