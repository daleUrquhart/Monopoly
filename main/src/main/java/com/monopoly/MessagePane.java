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
     * VBox Current Player display
     */
    private VBox currentPlayerDisplay;

    MessagePane() { 
        currentPlayerDisplay = new VBox();
    }

    /**
     * Displays the information on DispPane of main player
     * @param p Current player
     */
    void displayCurrent(Player current, GameController controller) {
        this.getChildren().clear();
        updateCurrentPlayerDislay(current, controller);
        this.add(currentPlayerDisplay, 0, 0); 
    }

    /**
     * Adds a message ontop of currentPlayerDisplay
     * @param message Message to be displayed
     */
    void showMessage(String message) {
        this.getChildren().clear();
        currentPlayerDisplay.getChildren().add(0, new Label(message));
        this.add(currentPlayerDisplay, 0, 0); 
        
    }

    
    /**
     * Gets the GUI Player display in a VBox
     */
    void updateCurrentPlayerDislay(Player current, GameController controller) {
        VBox display = new VBox();
        Label data = new Label(current.getName() + "'s turn\nBalance:" + current.getBalance() + "\n"); 
        HBox pBox;
        Button buyDevelopmentBoxB, mortgageB, unMortgageB, auctionB, privateSaleB, sellB, sellDevelopmentB; 

        display.getChildren().addAll(data);
        //display.getChildren().addAll(current.getPiece(), data); Try not adding piece to  view to see if it stays on board

        // Lengthy button display logic (dont offer to mortage an already mortgaged property, etc.)
        for(Property p : current.getProperties()) { 
            buyDevelopmentBoxB = new Button("Buy Development");
            buyDevelopmentBoxB.setOnMouseClicked(e -> p.buyDevelopment());

            mortgageB = new Button("Mortgage property");
            mortgageB.setOnMouseClicked(e -> p.mortgage());

            unMortgageB = new Button("Un Mortgage");
            unMortgageB.setOnMouseClicked(e -> p.unMortgage());

            // The next two are handled with controller as it requires additional inputs
            auctionB = new Button("Auction");
            auctionB.setOnMouseClicked(e -> controller.handleAuction(p));

            privateSaleB = new Button("Private Sale");
            privateSaleB.setOnMouseClicked(e -> controller.handlePrivateSale(p));

            sellB = new Button("Sell to Bank");
            sellB.setOnMouseClicked(e -> current.sell(p));

            sellDevelopmentB = new Button("Sell development");
            sellDevelopmentB.setOnMouseClicked(e -> p.sellDevelopment());

            pBox = new HBox();
            pBox.getChildren().add(new Label(p.toString()));

            if(!p.hasHotel() && current.canAfford(p.getDevelopmentCost())) {
                pBox.getChildren().add(buyDevelopmentBoxB);
            }
            if(!p.developed()) {
                if(!p.isMortgaged()) {
                    pBox.getChildren().add(mortgageB);
                }
                pBox.getChildren().addAll(auctionB, privateSaleB); 
            } else {

            }
            if(p.isMortgaged() && current.canAfford((int) (p.getMortgageValue() * 1.1))) {
                pBox.getChildren().add(unMortgageB);
            } 
            display.getChildren().add(pBox);
        } 
        
        currentPlayerDisplay = display;
    }

    
    /**
     * Removes currentPlayerDisplay form DispPane
     * 
     */
    void clearDispPane() {
        currentPlayerDisplay = new VBox(new Label(""));
    } 
}
