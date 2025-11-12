/**
 * Game Controller, highest level
 */
package com.monopoly;

import java.util.ArrayList;
import java.util.List; 

/**
 * Master class connecting Game and GameView tasks
 */
class GameController {

    /**
     * Manages the game instance
     */
    private final Game game;

    /**
     * Manages the viewer instance
     */
    private final GameView view;
 
    /**
     * 
     */
    private final PlayerBuilder pb;

    /**
     * Constructor for the controller
     * 
     * @param game Game instance
     * @param view Viewer instance
     */
    GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.pb = new PlayerBuilder(view.getMessagePane());
    }

    /**
     * Initialize the game and setup event handlers
     */
    void startGame() { 
        // Builds board components
        System.out.println("Placing tiles onto gameboard.");
        BoardBuilder bb = new BoardBuilder();  
        bb.buildTiles(game, view.getMainPane()); 
        view.setCenterPane(bb.buildCenter(game, view.getMainPane())); 
        
        // Builds and places dice grid onto center tile
        System.out.println("Placing dice onto center.");
        view.setDicePane(bb.buildDice(game, view.getMainPane())); 
        view.getDicePane().setOnMouseClicked(e -> game.handleRoll(view, this));  
 
        // Start player building process
        System.out.println("Beginging player building.");
        pb.initiatePlayerSetup();
        pb.loadPlayersToGame(game); 

        // Set a listener for when the player setup is complete
        pb.setPlayerSetupListener(() -> {
            pb.loadPlayersToGame(game);
            System.out.println("Players loaded, continuing game setup...");

            // Proceed with the game setup after players are loaded 
            view.getMessagePane().displayCurrent(game.getCurrentPlayer(), this); 
            view.showDice();
        });
    } 

    void handleAuction(Property property) {
        List<Player> bidders = game.getPlayers(); 
        Auction auction = new Auction(property, bidders);
        startAuction(auction, 0);
    }

    /**
     * Handles an auctoin action selection from the current player
     */
    void startAuction(Auction auction, int playerIndex) {
        Player current = auction.getBidders().get(playerIndex);
        MessagePane mp = view.getMessagePane();

        mp.getBoolInput(
            "Auction",
            current.getName() + ", bid on " + auction.getProperty().getName() + "?",
            "Current bid is $" + auction.getHighestBid(),
            wantsToBid -> {
                if (wantsToBid) {
                    mp.getIntInput(
                        "Enter Bid",
                        "Bid higher than current $" + auction.getHighestBid(),
                        "Your balance: $" + current.getBalance(),
                        auction.getHighestBid(),
                        current.getBalance(),
                        bid -> {
                            auction.placeBid(current, bid);
                            nextBidder(auction, playerIndex + 1);
                        }
                    );
                } else {
                    nextBidder(auction, playerIndex + 1);
                }
            }
        );
    }

    /**
     * Handle next bidder 
     */
    void nextBidder(Auction auction, int nextIndex) {
        if (nextIndex >= auction.getBidders().size()) {
            concludeAuction(auction);
        } else {
            startAuction(auction, nextIndex);
        }
    }

    /**
     * End of auction
     */
    void concludeAuction(Auction auction) {
        Player winner = auction.getHighestBidder();
        if (winner != null) {
            view.getMessagePane().showMessage(
                winner.getName() + " wins " + auction.getProperty().getName() +
                " for $" + auction.getHighestBid()
            );
            winner.buy(auction.getProperty(), auction.getHighestBid(), game);
        } else {
            view.getMessagePane().showMessage("No one bid. Property remains unsold.");
        }
    }


    /**
     * Handles a private sale action from the current player
     */
    void handlePrivateSale(Property property) { 
        boolean negotiating=true;
        int lastOffer = 0, offer;
        Player buyer, owner = (Player) property.getOwner();  
        
        //Get player
        buyer = selectPlayer(owner, property);

        //Get price
        while(negotiating) {
            //Get seller offer
            offer = GameView.getIntInput("Private Sale", 
                                        "Submit Offer (Last offer: "+lastOffer+")", 
                                        owner.getName() + ", what do you offer? (offer last offer value to quit negotiations) ", 
                                        lastOffer, 
                                        buyer.getBalance());
            if(offer != lastOffer) {
                //Propose offer
                if(GameView.getBoolInput("Private Sale", "The current offer on "+property.getName()+" is "+offer, "Do you accept this price?")) { 
                    buyer.buy(property, offer, game);
                } 
                else lastOffer = offer; 
            } 
            else negotiating = false; // Buyer quit negotiations 
        } 
    }

     /**
     * Example of using the showDialog method to select a player.
     * @param owner The owner of the property
     * @param location The property in question
     * @return The selected player
     */
    Player selectPlayer(Player owner, Property location) {
        // Create a list of available players
        List<Player> availablePlayers = new ArrayList<>();
        int minimum = location.isMortgaged() ? location.getMortgageValue() : 0;

        for (Player p : game.getPlayers()) {
            if (!p.equals(owner) && p.canAfford(minimum)) {
                availablePlayers.add(p);
            }
        }

        // Use the showDialog method to prompt for player selection
        return view.showDialog(
            "Select Player",
            "Select a player to sell the property to",
            "Available Players:",
            availablePlayers,
            availablePlayers.isEmpty() ? null : availablePlayers.get(0)
        );
    }
    
    /**
     * Handles the turn of landing on an unwoned property
     */
    void handleUnownedProperty() { 
        MessagePane mp = view.getMessagePane();
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();
        
        // If player can afford the property
        if(current.canAfford(property.getPrice())) { 
            mp.getBoolInput("Property", property.getName()+" is not owned yet.", "Would you like to buy it?",
                    result -> {
                        if(result) current.buy(property);
                        else handleAuction(property);
                    }
            );
        }

        // If the player has the net worth to afford the property
        else if (current.getNetWorth() >= property.getPrice()) {
            mp.getBoolInput(
                "Property",
                property.getName() + " is not owned yet.",
                "In order to purchase this property though, you will have to sell off assets. Would you like to buy it?",
                result -> {
                    if (result) {
                        current.liquidate(property.getPrice(), game);
                        current.buy(property);
                    } else handleAuction(property);
                }
            );
        }

        // The player can not afford the property
        else {
            mp.showMessage("\nThis property is not owned yet!\nYou can not afford this property though, and it will be going up for auction. ");
        }
    }

    /**
     * Handles game logic for landing on an owned property
     */
    void handleOwnedProperty() { 
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();
        Entity owner = property.getOwner();
        MessagePane mp = view.getMessagePane();

        if (!owner.equals(current)) {
            // Can not afford the rent
            if (!current.canAfford(property.getRent())) {
                mp.showMessage("\nBreaking! " + current.getName() + " bankrupted by: " + owner.getName() + "! ");
                if(game.getPlayerCount() != 2) current.bankrupted(owner, game);  
                else game.removePlayer(current);
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                mp.showMessage("\n"+current.getName() + " landed on " + owner.getName() + "'s property\nThe rent owed to them is $" + property.getRent() + ".");
            }
        }
        // If we own the property, do nothing
        mp.showMessage("\nYou are at " + property.getName() + ", and you own it already."); 
    }

    /**
     * Handles a jail turn
     * @return Whether or not they were freed by doubles
     */
    boolean handleJailTurn() {
        List<Integer> choices = game.getValidJailChoices();
        String message = game.getJailMessage();
        boolean freedByDoubles = false;

        // Display choices to the user via GameView
        Integer choice = view.showDialog(
            "Jail Decision",
            "You are in jail",
            message,
            choices,
            choices.get(0) // Default choice
        );

        // Handle choice
        if (choice != null) {
            freedByDoubles = game.handleJailChoice(choice);

            if (freedByDoubles) {
                GameView.showAlert("Success", "You rolled doubles! You are freed from jail.");
            } else {
                switch (choice) {
                    case 1: GameView.showAlert("Success", "You paid the fine and got out of jail.");
                    case 3: GameView.showAlert("Success", "You used a 'Get Out of Jail Free' card!");
                    case 2: GameView.showAlert("Failed", "You did not roll doubles. Jail turn incremented.");
                }
            }
        } else {
            GameView.showAlert("No Action", "You did not take any action and remain in jail.");
        }
        return freedByDoubles;
    }
}
