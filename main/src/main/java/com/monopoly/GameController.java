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
                game.handleRoll(view, this);
            } else System.out.println("Attemped roll while dice disabled");
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
            winner.buy(auction.getProperty(), auction.getHighestBid(), game, view.getMessagePane());
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
        MessagePane mp = view.getMessagePane();

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
                        "Private Sale Offer",
                        seller.getName() + " is offering to sell " + property.getName() +
                        " for $" + askingPrice + ".",
                        buyer.getName() + ", do you accept this offer?",
                        accepted -> {
                            if (accepted) {
                                if (buyer.canAfford(askingPrice)) {
                                    buyer.buy(property, askingPrice, game, mp);
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
            "Select Player",
            "Sell " + location.getName(),
            "Choose a player to sell to:",
            playerNames,
            selectedName -> {
                Player selectedPlayer = playerMap.get(selectedName);
                callback.accept(selectedPlayer);
            }
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
                        if(result) {
                            current.buy(property);
                            enableRoll();
                            mp.clearCurrentPlayerDisplay();
                            mp.displayCurrent(current, this);
                        }
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
                        enableRoll();
                    } else handleAuction(property);
                }
            );
        }

        // The player can not afford the property
        else {
            mp.showMessage("\nThis property is not owned yet!\nYou can not afford this property though, and it will be going up for auction. ");
            enableRoll();
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
                if(game.getPlayerCount() != 2) current.bankrupted(owner, game, mp);  
                else game.removePlayer(current);
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                mp.showMessage("\n"+current.getName() + " landed on " + owner.getName() + "'s property\nThe rent owed to them is $" + property.getRent() + ".");
            }
            enableRoll();
        }
        // If we own the property, do nothing
        else mp.showMessage("\nYou are at " + property.getName() + ", and you own it already.", () -> enableRoll());  
    }

    
    /**
     * Handle the choice selected from the options in getValidJailChoices()
     * @param choice Choice selected
     * @return Whether or not they were freed by doubles
     */
    boolean handleJailTurn(GameView view, Game game) { 
        MessagePane mp = view.getMessagePane(); 
        Jail jail = game.getJail();
        Player current = game.getCurrentPlayer();
        int bail = game.getBail();

        // --- Check if max turns reached ---
        if (current.getJailedTurns() >= 3) {
            if (current.canAfford(bail)) {
                current.debit(bail);
                jail.removePlayer(current);
                current.resetJailTurns();
                mp.showMessage(current.getName() + " failed to roll doubles after 3 turns and paid $" + bail + " bail.");
                view.showDice();
            } else {
                mp.showMessage(current.getName() + " cannot afford bail and has gone bankrupt!");
                game.bankruptPlayer(mp);
            }
            enableRoll();
            return false;
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
            "Jail Turn",
            "You are in jail.",
            "Choose how to proceed:",
            options,
            choice -> { 
                if (choice.startsWith("Pay Bail")) {
                    current.debit(50);
                    jail.removePlayer(current);
                    current.resetJailTurns();
                    mp.showMessage("You paid your bail and are free!");
                    view.showDice();
                } 
                else if (choice.equals("Use Get Out of Jail Free Card")) {
                    current.decrementJailCard();
                    jail.removePlayer(current);
                    current.resetJailTurns();
                    mp.showMessage("You used your Get Out of Jail Free card and are free!");
                    view.showDice();
                } 
                else if (choice.equals("Try for Doubles")) {
                    int roll = game.getDice().roll(current);
                    mp.showMessage("You rolled a " + roll + "!");
                    boolean isDoubles = game.getDice().doubles();

                    if (isDoubles) {
                        jail.removePlayer(current);
                        current.setLocation(game.getSpace((current.getLocation().getId() + roll) % game.getMap().length));
                        current.resetJailTurns();
                        mp.showMessage("You rolled doubles and are free!");
                        view.showDice();
                    } else {
                        current.incrementJailTurns();
                        if (current.getJailedTurns() >= 3) {
                            if (current.canAfford(50)) {
                                current.debit(50);
                                jail.removePlayer(current);
                                current.resetJailTurns();
                                mp.showMessage("Three turns passed. You paid bail and are free.");
                                view.showDice();
                            } else {
                                mp.showMessage(current.getName() + " cannot pay bail and is bankrupted.");
                                current.bankrupted(Banker.getInstance(), game, mp);
                            }
                        } else {
                            mp.showMessage("No doubles. You remain in jail (" + current.getJailedTurns() + "/3).");
                        }
                    }
                } 
                else {
                    mp.showMessage("Unknown choice.");
                }
            }
        );
 
        return true;
    }
    
    /**
     * Handles game logic for landing on any of the special squares
     */
    void handleSpecialSquare(Game game, GameController controller) {  
        Player current = game.getCurrentPlayer();
        BoardSpace location = current.getLocation(); 
        Tax tax;
        CardManager cm;
        Go go;
        Jail jail = game.getJail(); 
        Card card; 
        MessagePane mp = view.getMessagePane();

        if (location instanceof Go) {
            go = (Go) location;
            go.reward(current);
            mp.showMessage("Congratulations, " + current.getName() + "! You made it to Go! ");
            enableRoll();
        } 
        
        else if (location instanceof Jail) {
            jail = (Jail) location;
            if (jail.hasJailed()) {
                mp.showMessage("Welcome to the visitation center. Say hello to your friends. ", () -> enableRoll());
            } else {
                mp.showMessage("Welcome to the visitation center. Better stay on the right side of these bars...", () -> enableRoll());
            }
        } 
        
        else if (location instanceof FreeParking) {
            mp.showMessage("Welcome to free parking. Take a breather. ");
            enableRoll();
        } 
        
        else if (location instanceof GoToJail) {  
            jail.addPlayer(current);            
            mp.showMessage("Go directly to Jail. Do not pass Go, do not collect $200! ");
            if(game.getDice().doubles()) {game.increment(game.getTurnIndex());} //Do not go again from doubles if landed on go to jail, re-increment turn index
            enableRoll();
        } 
        
        else if (location instanceof Tax) {
            tax = (Tax) location;
            //If player can afford the tax pay it
            if(current.canAfford(tax.getTax())) {
                tax.charge(current);
                mp.showMessage("Uh oh! You have been charged "+tax.getName()+"! You were charged $" + tax.getTax() + "!");
            } 
            //Liquidate asssets to pay for taxes
            else if(!current.canAfford(tax.getTax()) && current.getNetWorth() >= tax.getTax()) {
                current.liquidate(tax.getTax(), game);
                mp.showMessage("Breaking! " + current.getName() + " can not afford their taxes and goes bankrupt! It was a good run"); 
            }
            //Player bankrupted by bank, not able ot pay thier taxes
            if(game.getPlayerCount() == 2) {game.removePlayer(current);}
            else current.bankrupted(Banker.getInstance(), game, mp);      
            enableRoll();
        } 
        
        else if (location instanceof CardManager) {
            cm = (CardManager) location;
            card = cm.draw(game);
            mp.showMessage("Welcome to the "+location.getName()+" square! Your card draw is:\n"+ card.toString());
            CardManager.handle(card, game, controller); 
            enableRoll();
        }  
    } 
}