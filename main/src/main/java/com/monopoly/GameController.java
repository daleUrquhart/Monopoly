/**
 * Game Controller, highest level
 */
package com.monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer; 

/**
 * Master class connecting Game and GameView tasks
 * 
 * Responsible for: Prompting UI updates, 
 * Not responsible for: Handling game rules, using anything requiring JavaFX, 
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
 
    /*
     * 
     */
    MessagePane mp;

    /**
     * 
     */
    private final PlayerBuilder pb;

    /**
     * Enabled once turn actions are all resolved
     */
    private Boolean rollEnabled;

    /**
     * Constructor for the controller
     * 
     * @param game Game instance
     * @param view Viewer instance
     */
    GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.mp = view.getMessagePane();
        this.rollEnabled = true;
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
        view.getDicePane().setOnMouseClicked(e -> {
            if(rollEnabled) {
                disableRoll();
                handleRoll();
            } else System.out.println("Attemped roll while dice disabled");
        });  
 
        // Builds barsPane
        System.out.println("Building bars pane.");
        view.setJailPane(bb.buildBars(game, view.getMainPane())); 
        view.getJailPane().setOnMouseClicked(e -> {
            if(rollEnabled) {
                disableRoll();
                startJailTurn();
            } else System.out.println("Attemped click while bars disabled");
        });  

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

    /**
     * Enabled rolling
     */
    void enableRoll() {
        rollEnabled = true;
    }

    /**
     * Disables rolling
     */
    void disableRoll() {
        rollEnabled = false;
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

        mp.getBoolInput( 
            current.getName() + ", bid on " + auction.getProperty().getName() + "?\n"+
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
                winner.getName() + " wins " + auction.getProperty().getName() +" for $" + auction.getHighestBid());
            buy(winner, Banker.getInstance(), auction.getProperty(), auction.getHighestBid(), game, view.getMessagePane());
        } else {
            view.getMessagePane().showMessage("No one bid. Property remains unsold.");
        }
        enableRoll();
    }


    /**
     * Handles a private sale action from the current player
     */
    void handlePrivateSale(Property property) {
        Player seller = (Player) property.getOwner(); 

        // Ask seller to select a buyer
        selectPlayer(seller, property, buyer -> {
            if (buyer == null) {
                mp.showMessage("No available players to sell this property to.");
                enableRoll();
                return;
            }

            // Ask seller to set asking price
            mp.getIntInput(
                "Private Sale",
                "Selling " + property.getName(),
                "Enter the asking price for " + buyer.getName() + ":",
                1, // minimum
                seller.getBalance(), // maximum 
                askingPrice -> {
                    if (askingPrice <= 0) {
                        mp.showMessage("Invalid price. Sale cancelled.");
                        enableRoll();
                        return;
                    }

                    // Prompt buyer to accept or reject
                    mp.getBoolInput( 
                        seller.getName() + " is offering to sell " + property.getName() +
                        " for $" + askingPrice + ".\n"+ buyer.getName() + ", do you accept this offer?",
                        accepted -> {
                            if (accepted) {
                                if (buyer.canAfford(askingPrice)) {
                                    buy(buyer, seller, property, askingPrice, game, mp);
                                    mp.showMessage(
                                        buyer.getName() + " purchased " + property.getName() +
                                        " from " + seller.getName() + " for $" + askingPrice + "."
                                    );
                                } else {
                                    mp.showMessage(
                                        buyer.getName() + " cannot afford this offer."
                                    );
                                }
                            } else {
                                mp.showMessage(
                                    buyer.getName() + " declined the offer for " + property.getName() + "."
                                );
                            }
                            enableRoll();
                        }
                    );
                }
            );
        });
    }

    /**
     * Handles player selection of a private sale
     * Selected player gets the option to either accept the sale or deny
     * @param owner
     * @param location
     * @param callback
     */
    void selectPlayer(Player owner, Property location, Consumer<Player> callback) {
        List<Player> availablePlayers = new ArrayList<>();
        int minimum = location.isMortgaged() ? location.getMortgageValue() : 0;

        for (Player p : game.getPlayers()) {
            if (!p.equals(owner) && p.canAfford(minimum)) {
                availablePlayers.add(p);
            }
        }

        if (availablePlayers.isEmpty()) {
            view.getMessagePane().showMessage("No players can afford this property right now.");
            callback.accept(null);
            return;
        }

        // Build mapping of string representations to Player objects
        Map<String, Player> playerMap = new HashMap<>();
        List<String> playerNames = new ArrayList<>();
        for (Player p : availablePlayers) {
            String display = p.getName() + " ($" + p.getBalance() + ")";
            playerMap.put(display, p);
            playerNames.add(display);
        }

        // Use your existing getChoiceInput
        view.getMessagePane().getChoiceInput( 
            "Sell " + location.getName()+
            "\nChoose a player to sell to:",
            playerNames,
            selectedName -> {
                Player selectedPlayer = playerMap.get(selectedName);
                callback.accept(selectedPlayer);
            }
        );
    }  

    void processNextTurn() {
        // Assign next player
        game.advanceTurn();  
        view.hidePrompt();

        /* 
        If next player is in jail, replace dice with jail bars.
        Once bars are clicked jail turn is handled.
        */
        if(game.getCurrentPlayer().inJail()) view.showJail(); 
        /*
        If player is not in jail, show dice.
        Once dice are clicked, roll is made and new tile is processed
        */
        else view.showDice();
    }

    /**
     * Handles a roll of the dice
     */
    void handleRoll() {     
        Player current = game.getCurrentPlayer();
        
        mp.clear();
        mp.displayCurrent(current, this);
        disableRoll(); 

        // Make roll and assign the new location
        int roll = game.getDice().roll(current);  
        int newSpaceID = roll + current.getLocation().getId(); 

        // Passed Go
        if(game.passedGo(newSpaceID)) {
            game.getGo().reward(current);
            mp.showMessage("\nYou passed Go! Here is $200.");
        }

        // Assign new location
        game.movePlayerTo(current, newSpaceID);
        BoardSpace newSpace = current.getLocation();
        mp.showMessage("\nYou rolled a "+roll+" and landed on "+newSpace.getName());   

        // Handle Doubles logic 
        switch (game.getDoublesOutcome()) {
            case -1:
                mp.showMessage("\nYou rolled doubles, you get to roll again after your turn! ");
                break; 
            case 1:
                // Third doubles in a row, add current to jail and end the turn
                mp.showMessage("\nThat was your third doubles, go to jail! ");
                return; 
        }

        // Handle the logic for landing on the new location 
        if(newSpace instanceof Property) {
            handleProperty();
            
        } else {
            handleSpecialSquare();
        } 
    } 

    void handleProperty() {
        if(((Property) game.getCurrentPlayer().getLocation()).isOwned()) {
            handleOwnedProperty();
        } else {
            handleUnownedProperty();
        } 
        processNextTurn(); 
    }

    /**
     * Handles the turn of landing on an unwoned property
     */
    void handleUnownedProperty() {  
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();

        // If player's networth exceeds property price show option to buy
        if(current.getNetWorth() >= property.getPrice()) { 
            mp.getBoolInput("\n"+property.getName()+" is not owned yet." +" Would you like to buy it?",
                    result -> {
                        // player chose to buy with sufficent cash
                        if(result && current.canAfford(property.getPrice())) {
                            property.sellTo((Entity) current, property.getPrice());
                            enableRoll();
                            mp.displayCurrent(current, this);
                        }
                        // Player chose to buy property but hsa insufficent cash, leave option to sell assets and buy
                        else if(result && !current.canAfford(property.getPrice())) {
                            mp.clearMessages();
                            mp.showAck("Submit Payment", () -> handleUnownedProperty());
                            mp.showMessage("You can not afford this, liquidate assets to purchase");
                        }
                        // Player chose not to buy, property goes to auction
                        else {
                            mp.showMessage("\nYou chose not to buy this property, and it will be going up for auction. ");
                            handleAuction(property);
                        }
                    }
            );
        } 

        // The player can not afford the property, goes to auction
        else {
            mp.showMessage("\nYou can not afford this property, and it will be going up for auction. ");
            handleAuction(property);
        }
    }

    /**
     * Handles game logic for landing on an owned property
     */
    void handleOwnedProperty() { 
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();
        Entity owner = property.getOwner(); 
        int rent = property.getRent();

        if (!owner.equals(current)) {
            // Can not afford the rent with assets
            if (current.getNetWorth() < rent) {
                game.bankruptPlayer(current, owner, this);
                mp.showMessage("\nBreaking! " + current.getName() + " bankrupted by: " + owner.getName() + "! "); 
                mp.displayCurrent(current, this);
                enableRoll();
            }
            // Can not afford the rent with current balance, pause game until enough assets are sold
            else if(!current.canAfford(rent)) {
                mp.clearMessages();
                mp.showAck("Submit Payment", () -> handleOwnedProperty());
                mp.showMessage("G\name may not proceed until "+property.getName()+"'s' rent is paid by "+current.getName()+". Liquidate assets to afford the $"+rent+" rent.");
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                mp.showMessage("\n"+current.getName() + " landed on " + owner.getName() + "'s property\nThe rent owed to them is $" + rent + ".");
                mp.displayCurrent(current, this);
                enableRoll();
            }
        }
        // If we own the property, do nothing
        else {
            mp.showMessage("\nYou are at " + property.getName() + ", and you own it already.");  
            enableRoll();
        }
    } 
    
    /**
     * Handle the choice selected from the options in getValidJailChoices()
     * @param choice Choice selected
     * @return Whether or not they were freed by doubles
     */
    void startJailTurn() {  
        Jail jail = game.getJail();
        Player current = game.getCurrentPlayer();
        int bail = jail.getBail();
 
        mp.clear();
        mp.displayCurrent(current, this);
        disableRoll();

        // --- Handle max jail turns ---
        if (current.getJailedTurns() >= 3) { 
            handleMaxJailTurns(view, this);  
            processNextTurn(); 
            return; 
        }

        // --- Build available options ---
        List<String> options = new ArrayList<>();
        options.add("Try for Doubles");
        if (current.canAfford(bail)) {
            options.add("Pay Bail ($" + bail + ")");
        }
        if (current.ownsJailCard()) {
            options.add("Use Get Out of Jail Free Card");
        }

        // --- Prompt player for choice ---
        mp.getChoiceInput( 
            "You are in jail. Choose how to proceed:",
            options,
            choice -> { 
                if (choice.startsWith("Pay Bail")) {
                    game.processBail();
                    mp.showMessage("You paid your bail and are free!");
                    mp.displayCurrent(current, this);
                } 
                else if (choice.equals("Use Get Out of Jail Free Card")) {
                    game.processJailCard();
                    mp.showMessage("You used your Get Out of Jail Free card and are free!");
                } 
                else if (choice.equals("Try for Doubles")) {
                    Boolean outcome = game.processDoubles();
                    mp.showMessage("You rolled a " + game.getDice().getRoll() + "!");
                    if(outcome) mp.showMessage("You rolled doubles and are free!");
                    else {
                        current.incrementJailTurns();
                        mp.showMessage("No doubles. You remain in jail (" + current.getJailedTurns() + "/3)."); 
                    }
                } 
                enableRoll(); 
            }
        );

        processNextTurn(); 
    }
    
    /**
     * Handles the event of maximum jail turns reached
     * If they can use a jail card sue it, else pay bail, if cant afford liquidate assets, if insufficent assets bankrupt
     */
    void handleMaxJailTurns(GameView view, GameController controller) { 
        Player current = game.getCurrentPlayer();
        Jail jail = game.getJail();
        int bail = jail.getBail();
 
        // If Player have a jail card, use it for them
        if (current.ownsJailCard()) {
            game.processJailCard();
            mp.showMessage("Jailcard utilized, you are free from jail");
        } 
        // If Player can afford bail fee, charge it automatically
        else if (current.canAfford(bail)) {
            game.processBail();
            mp.showMessage(current.getName() + " failed to roll doubles after 3 turns and was charged $" + bail + " bail.");
            enableRoll();
        } 
        // If player has the net worht to pay for bail, pause game until they pay the bail
        else if (current.getNetWorth() >= bail) { 
            mp.clearMessages(); 
            mp.showAck("Submit Payment", () -> handleMaxJailTurns(view, controller));
            mp.showMessage("Game may not proceed until bail is paid by "+current.getName()+". Liquidate assets to afford the "+bail+" bail.");
        } else {
            mp.showMessage(current.getName() + " cannot afford bail and has gone bankrupt!");
            game.bankruptPlayer(current, Banker.getInstance(), controller);
            enableRoll();
        }
    } 

    /**
     * Handles game logic for landing on any of the special squares
     */
    void handleSpecialSquare() {  
        Player current = game.getCurrentPlayer();
        BoardSpace location = current.getLocation();   

        location.onLand(current, game, mp, this);
        mp.displayCurrent(current, this);
        enableRoll();     
        processNextTurn(); 
    } 

    /**
     * Handles game end state
     * Just deletes dice so nobody can go anymore to gracefully 'end game'
     */
    void handleWinner() { 
        mp.clear(); 
        mp.showMessage(game.getCurrentPlayer().getName() + " wins the game!");
        mp.displayCurrent(game.getCurrentPlayer(), this);
        view.deletePrompts();
    }

    /**
     * Handles the purchasing of a mortgaged property
     */
    void handleMortgagedPurchase(Player bankrupter, Property p) { 
        mp.getBoolInput(p.toString()+" is mortgaged. Would you like to unmortgage it now, for "+(int) ((double) p.getMortgageValue() * 1.1)+", or wait until later and only pay the current intrest owing of "+(int) ((double) p.getMortgageValue() * 0.1)+".",
            (result) -> {
                if(result) {
                    game.payMortgage(bankrupter, p);
                } else {
                    mp.showMessage("Property remains mortgaged, intrest only payment made. ");
                    game.payMortgageIntrest(bankrupter, p);
                }
            }
        ); 
    }

    /**
     * Handles the purchase of a new proerty for the plaeyr bought by auction, or private sale
    * @param newProperty the property to buy
    * @param bid the amount the player bid for the property  
    */
    void buy(Player buyer, Entity seller, Property p, int bid, Game game, MessagePane mp) { 
        //This is all only unmortgaging or paying intrest
        if(p.isMortgaged()) handleMortgagedPurchase(buyer, p);

        //Bid transactioning
        p.sellTo(buyer, bid); 
    }
}