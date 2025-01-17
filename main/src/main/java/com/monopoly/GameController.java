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
        this.pb = new PlayerBuilder(view.getDispPane());
    }

    /**
     * Initialize the game and setup event handlers
     */
    void startGame() { 
        // Builds board components
        BoardBuilder bb = new BoardBuilder();  
        bb.buildTiles(game, view.getMainPane()); 
 
        view.setCenterPane(bb.buildCenter(game, view.getMainPane())); 
 
        view.setDicePane(bb.buildDice(game, view.getMainPane())); 
        view.getDicePane().setOnMouseClicked(e -> game.handleRoll(view, this));  
 
        // Start player building process
        pb.initiatePlayerSetup();
        pb.loadPlayersToGame(game); 

        // Set a listener for when the player setup is complete
        pb.setPlayerSetupListener(() -> {
            pb.loadPlayersToGame(game);
            System.out.println("Players loaded, continuing game setup...");

            // Proceed with the game setup after players are loaded 
            view.displayCurrent(game.getCurrentPlayer(), this); 
            view.showDice();
        });
    } 

    /**
     * Handles an auctoin action selection from the current player
     */
    void handleAuction(Property location) { 
        int bid = 1, passedTurns = 0, turn = game.increment(game.getTurnIndex()), attempt;
        boolean open = true, yes;
        Player bidder = game.getPlayer(turn); 
        Entity highestBidder = location.getOwner(); 

        //Per player:
        while(open) {
            yes = false;

            //Do you want to bid?
            if(bid < bidder.getBalance() && !bidder.equals(location.getOwner())) {
                yes = GameView.getBoolInput("Auction", 
                                            bidder.getName()+", "+location.getName()+" is up for auction, would you like to make a bid?", 
                                            "The current bid is at $"+bid);
            }

            //Yes?
            if(yes) {
                passedTurns = 0;  
                attempt = GameView.getIntInput("Bid",
                                                    "Current Bid: " + bid + ", enter a value higher than the current bid to take the lead in the auction, or current bid amount to cancel your bid attempt",
                                                    "Current highest bidder is "+highestBidder.getName(),
                                                    bid,
                                                    bidder.getBalance());
                if (attempt > bid) highestBidder = bidder;
            }

            if(passedTurns == game.getPlayerCount()) {
                //Auction concluded
                open = false;
            } else {
                //Assign next bidder
                turn = game.increment(turn);
                bidder = game.getPlayer(turn);
            }
        }

        //Property sold by bank at auction to a player
        if(highestBidder instanceof Player && !(location.getOwner() instanceof Player)) {
            view.showMessage("\nBidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
            highestBidder.buy(location, bid, game); 
        }
        //Player auctioning property off to other players
        else if(highestBidder instanceof Player && location.getOwner() instanceof Player) {
            if(GameView.getBoolInput("Auction", "The highest bid was "+bid, "Do you want to accept that amount, "+location.getOwner().getName()+", or keep the property? ")) {
                view.showMessage("\nBidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
                highestBidder.buy(location, bid, game); 
            } else {
                view.showMessage("\nOwner disatisfied with acution, recants property. "); 
            }
        }
        //Property stays with the bank
        else {
            view.showMessage("\nNo bids made, "+location.getName()+" stays with "+location.getOwner().getName()+". ");
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
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();
        
        // If player can afford the property
        if(current.canAfford(property.getPrice())) { 
            if (GameView.getBoolInput("Property", property.getName()+" is not owned yet.", "Would you like to buy it?")) {
                current.buy(property); 
            }
        }

        // If the player has the net worth to afford the property
        else if(current.getNetWorth() >= property.getPrice()) {
            if (GameView.getBoolInput("Property", property.getName()+" is not owned yet.", "In order to purchase this property though, you will have to sell off assets. Would you like to buy it?")) {
                current.liquidate(property.getPrice(), game);
                current.buy(property); 
            }
        }
        // The player can not afford the property
        else {
            GameView.showAlert("\nThis property is not owned yet!", "You can not afford this property though, and it will be going up for auction. ");
        }
        if(!current.equals(property.getOwner())) handleAuction(property);
    }

    /**
     * Handles game logic for landing on an owned property
     */
    void handleOwnedProperty() { 
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();
        Entity owner = property.getOwner();
        
        if (!owner.equals(current)) {
            // Can not afford the rent
            if (!current.canAfford(property.getRent())) {
                GameView.showAlert("\nBreaking! " + current.getName() + " bankrupted by: " + owner.getName() + "! ", "");
                if(game.getPlayerCount() != 2) current.bankrupted(owner, game);  
                else game.removePlayer(current);
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                GameView.showAlert("\n"+current.getName() + " landed on " + owner.getName() + "'s property", "The rent owed to them is $" + property.getRent() + ".");
            }
        }
        // If we own the property, do nothing
        else GameView.showAlert("\nYou are at " + property.getName(), "And you own it already."); 
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
