/**
 * Contatins high level play functions for Monopoly
 * @author Dale Urquhart
 */


package com.monopoly;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional; 

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; 

/**
 * High level handler class for Monopoly funcitons
 */
final class Game {

    /**
     * Path to resources
     */
    private static final String PATH = "../resources/com/monopoly/";

    /**
     * Bail amount
     */
    private static final int BAIL = 50;

    /**
     * Collection of boardspaces in order
     */
    private BoardSpace[] map; 
 
    /**
     * Represents the number of players playing in the instance of the game
     */
    private int playerCount;

    /**
     * Array of the players in the game
     */
    private ArrayList<Player> players;

    /**
     * Index of Player in players for whose turn it is
     */
    private int turnIndex;
 
    /**
     * Dice for the game (represents two dice rolled together)
     */
    private Dice dice;

    /**
     * Community Chest deck
     */
    private final List<Card> cCDeck;

    /**
     * Chance Deck
     */
    private final List<Card> chanceDeck;

    /**
     * Manages the current player
     */
    private Player current; 

    /**
     * Primary label for handling rudementary instruction
     */
    private final Label genLabel;  

     /**
     * HBox for storing primary label, field and submit
     */
    private final HBox genBox; 

    /**
     * GamePane
     */
    private final GridPane gamePane;

    /**
     * Game constructor
     */
    Game(GridPane gamePane){ 
        turnIndex = 0; 
        
        genLabel = new Label(); 
        genBox = new HBox(genLabel);
        this.gamePane = gamePane; 
        this.gamePane.getChildren().add(genBox);

        players = new ArrayList<> (); 
        players = new ArrayList<>();

        List<List<Card>> decks = buildDecks();
        chanceDeck = decks.get(0);
        cCDeck = decks.get(0); 
        System.out.println("\tDecks built succesfully..."); 

        map = buildMap();
        System.out.println("\tMap built succesfully..."); 
    }

    /**
     * Gets the space index from map
     * @param index
     */
    BoardSpace getSpace(int i) {
        return map[i];
    }
   
    /**
     * Gets the board map
     * @return the board map
     */
    BoardSpace[] getMap() {
        return map;
    } 

    /**
     * gets the bail amount
     * @return the bail amount
     */
    int getBail() {
        return BAIL;
    }

    /**
     * Gets teh chance deck
     */
    List<Card> getChanceDeck() {
        return chanceDeck;
    } 

    /**
     * Gets the community chest deck
     */
    List<Card> getCommunityChestDeck() {
        return cCDeck;
    }
 
    /**
     * Gets array of all players
     * @return array of players
     */
    ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for the player count
     * @return the number of players in the game
     */
    int getPlayerCount() {
        return playerCount;
    }

    /**
     * Returns the current player
     * @return the current player
     */
    Player getCurrentPlayer() {
        return current;
    }

    /**
     * Getter for the turn index
     * @return the index of players array for the current player
     */
    int getTurnIndex() {
        return turnIndex;
    }

    /**
     * Returns the indexed player from players
     * @param i index of the player
     * @return the player indexed
     */
    Player getPlayer(int i) {
        return players.get(i);
    }

    /**
     * Gets the Go space
     */
    Go getGo() {
        return (Go) getSpace(0);
    }

    /**
     * Gets the next player and increments turn index, returns null if there is only one non-bankrupt player left
     * @return Player next player to play
     */
    Player getNextPlayer() {  
        getCurrentPlayer().flipCurrent();
        current = getPlayers().get(incrementTurnIndex());
        current.flipCurrent();
        return getCurrentPlayer();
    }

    /**
     * Gets the game dice
     */
    Dice getDice() {
        return dice;
    } 
 
    /**
     * Gets the jail instance
     * @return the jail instance for the game
     */
    Jail getJail() {
        return (Jail) getSpace(10);
    }

    /**
     * Sets the player count
     * @param count
     */
    void setPlayerCount(int count) {
        playerCount = count;
    }
    
    /**
     * Assigns the dice for the game
     * @param dicePane Graphic representation for the dice
     */
    void setDice(Dice dice) {
        this.dice = dice;
        System.out.println("\tDice built succesfully...");
    }

    /**
     * Sets the current player
     */
    void setCurrentPlayer(Player current) {
        this.current = current;
    }
 
    /**
     * Builds Chance Decks.
     */
    List<List<Card>> buildDecks() {
        List<List<Card>> decks = new ArrayList<>(2);
        try {
            decks.add(Card.getChanceDeck(PATH + "cards.csv"));
            decks.add(Card.getCCDeck(PATH + "cards.csv"));
            return decks;
        } catch (IOException e) {
            System.out.println("Come on, idiot. Give me a good csv");
            return new ArrayList<>(2);
        }
    }

    /**
     * Builds the game map
     */
    public BoardSpace[] buildMap() {
        map = new BoardSpace[40]; 
        try (BufferedReader br = new BufferedReader(new FileReader(PATH+"properties.csv"))) { 
            String line;
            Banker banker = Banker.getInstance();

            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); 
                int index = Integer.parseInt(values[0]);
                String type = values[1];
                String name = values[2];
                String group = values[3];
                int price = Integer.parseInt(values[4]);
                String rentStructure = values[5];
                String action = values[6];  
                
                switch (type) {
                    case "Go":
                        map[index] = new Go("Go", 0);
                        for(Player p : getPlayers()) {
                            p.setLocation(map[index]);  
                        }
                        break;
                    case "Property":
                        String[] rents = rentStructure.split(";");
                        int[] rentArray = new int[rents.length];
                        for (int i = 0; i < rents.length; i++) {
                            rentArray[i] = Integer.parseInt(rents[i]);
                        }
                        map[index] = new Property(banker, name, group, 0, index, rentArray[0], rentArray[1], rentArray[2],
                                rentArray[3], rentArray[4], rentArray[5], price / 2, 50, price);
                        break;
                    case "Railroad":
                        map[index] = new Railroad(name, index, price, banker);
                        break;
                    case "Utility": 
                        map[index] = new Utility(name, index, price, banker);
                        break;
                    case "Jail":
                        map[index] = new Jail("Jail", 10);
                        break;
                    case "Tax":
                        int taxAmount = Integer.parseInt(action);
                        map[index] = new Tax(name, index, taxAmount);
                        break;
                    case "CardManager":
                        map[index] = new CardManager(name, index,
                                action.equals("Chance") ? getChanceDeck() : getCommunityChestDeck());
                        break;
                    case "FreeParking":
                        map[index] = new FreeParking();
                        break;
                    case "GoToJail":
                        map[index] = new GoToJail((Jail) map[10]);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Bad csv, idiot"); 
        } catch (Exception e) {
            System.out.println("Are you even reading this thing? or just guessing?");
            throw(e);
        }
        return map;
    }

    /**
     * Removes a player
     * @param p player to remove
     */
    void removePlayer(Player p) {
        getPlayers().remove(p);
        playerCount--;
    }

    /**
     * Adds a player to ArrayList<Player> players
     * @param p player to add to players
     */
    void addPlayer(Player p) {
        players.add(p); 
    }

    /**
     * Decrements the turn index and will index the previously played player
     */
    void decrementTurnIndex() {
        turnIndex = getTurnIndex() == -1 ? getPlayerCount()-1 : turnIndex - 1;
    }

    /**
     * Increments the turn index
     * @return the new turn index
     */
    int incrementTurnIndex() {  
        turnIndex = getTurnIndex() == (getPlayerCount() - 1) ? 0 : turnIndex + 1; 
        return turnIndex;
    }

    /**
     * Raises an alert and returns its input
     * @param title Title for alert raised
     * @param header Header for alert raised
     * @param context Context for alert raised
     * @return true for if teh response is 'yes'
     */
    public boolean boolInput(String title, String header, String context) {
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
     * Raises a dialog to input an integer and validates it against a given range.
     * @param title Title for the input dialog
     * @param header Header for the input dialog
     * @param context Context for the input dialog
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return The valid integer input from the user
     */
    public int intInput(String title, String header, String context, int min, int max) {
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
     * Displays an error dialog with a given title and message.
     * @param title Title for the error dialog
     * @param message Message for the error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the aucitoning of a property
     * @param location the property we are auctioning
     * @return true for if there was a succesful transfer of property
     */
    boolean handleAuction(Property location) {
        int bid = 1, passedTurns = 0, turn = 0, attempt;
        boolean open = true, goodInt, yes;
        Player bidder = getPlayer(turn); 
        Entity highestBidder = location.getOwner(); 

        //Per player:
        while(open) {
            yes = false;

            //Do you want to bid?
            if(bid < bidder.getBalance() && !bidder.equals(location.getOwner())) {
                yes = boolInput("Auction", bidder.getName()+", "+location.getName()+" is up for auction, would you like to make a bid?", "The current bid is at $"+bid);
            }

            //Yes?
            if(yes) {
                passedTurns = 0; goodInt = false;
                System.out.print("Enter your bid: $");
                //Validate integer input
                while(!goodInt) {
                    try {
                        attempt = intInput("Auction", bidder.getName()+", enter your bid for "+location.getName(), "Current bid is at "+bid, bid, bidder.getBalance());
                        if(attempt > bid && attempt <= bidder.getBalance()) {
                            bid = attempt;
                            highestBidder = bidder;
                            goodInt = true;
                        } else if(attempt <= bid) {
                            System.out.print("Going to have to try harder than that... ");
                        } else if(attempt > bidder.getBalance()) {
                            System.out.print("Try it again, that number is too rich for you... ");
                        }
                    } catch (InputMismatchException  e) {
                        System.out.print("That is not a valid input.. ");
                    }
                }
            } else {passedTurns += 1;} //Palyer did not bid

            if(passedTurns == getPlayerCount()) {
                //Auction concluded
                open = false;
            } else {
                //Assign next bidder
                turn = turn == (getPlayerCount()-1) ? 0 : turn + 1;
                bidder = getPlayer(turn);
            }
        }

        //Property sold by bank at auction to a player
        if(highestBidder instanceof Player && !(location.getOwner() instanceof Player)) {
            genLabel.setText(genLabel.getText()+"\nBidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
            highestBidder.buy(location, bid, this);
            return true;
        }
        //Player auctioning property off to other players
        else if(highestBidder instanceof Player && location.getOwner() instanceof Player) {
            if(boolInput("Auction", "The highest bid was "+bid, "Do you want to accept that amount, "+location.getOwner().getName()+", or keep the property? ")) {
                genLabel.setText(genLabel.getText()+"\nBidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
                highestBidder.buy(location, bid, this);
                return true;
            } else {
                genLabel.setText(genLabel.getText()+"\nOwner disatisfied with acution, recants property. ");
                return false;
            }
        }
        //Property stays with the bank
        else {
            genLabel.setText(genLabel.getText()+"\nNo bids made, "+location.getName()+" stays with "+location.getOwner().getName()+". ");
            return false;
        }
    } 

    /**
     * Handles dice rolling and player movement logic 
     */
    void handleRoll(GridPane primary) {
        try {  
            //Remove general player data display
            gamePane.getChildren().remove(primary);

            // Make roll and assign the new location
            int roll = getDice().roll(current);  
            int newSpace = roll + current.getLocation().getId();
            genLabel.setText("You rolled a "+roll+"!");

            // Passed Go
            if(newSpace >= getMap().length) {
                genLabel.setText(genLabel.getText()+"\nYou passed Go! Here is $200.");
                newSpace -= getMap().length; 
                getGo().reward(getCurrentPlayer()); 
            }

            // Assign new location
            current.setLocation(getSpace(newSpace));

            // Got doubles
            if (getDice().doubles()) {
                // If third doubles in a row, go to jail
                if (current.getDoubleCount() == 2) {
                    genLabel.setText(genLabel.getText()+"\nThat was your third doubles, go to jail! ");
                    current.flipJailed();
                    current.resetDoubleCount();
                }
                // If not third doubles in a row, roll again
                else {
                    genLabel.setText(genLabel.getText()+"\nYou rolled doubles, you get to roll again after your turn! ");
                    current.incrementDoubleCount();
                    decrementTurnIndex();
                }
            }

            // Handle the logic for landing on the new location 
            handleNewLocation();   

            // Did not get doubles
            if(!getDice().doubles()) {
                current.resetDoubleCount(); 
                current = getNextPlayer(); 
            } 

        } catch(NullPointerException e) {
            System.err.println("Null pointer exception in Game.handleRoll()\n"+e);
        } 

        // Clear the Game instance vars from the gamePane
        gamePane.getChildren().removeAll(genLabel, genBox);
    }

    /**
     * Handles the private sale of a property
     * @return true for if there was a succesful transfer of property
     */
    boolean handlePrivateSale(Property property) { 
        boolean negotiating=true, goodInt=false;
        int lastOffer = Integer.MAX_VALUE, offer=0;
        Player player, owner = (Player) property.getOwner();  
        
        //Get player
        player = selectPlayer(owner);

        //Get price
        while(negotiating) {
            //Get seller offer
            System.out.print(owner.getName() + ", what do you offer? (offer -1 to quit negotiations) ");
            while(!goodInt) {
                try {
                    offer = intInput("Private Sale", owner.getName() + ", what do you offer? (offer -1 to quit negotiations) ", "Enter your offer", -1, owner.getBalance());
                    if(offer == -1) {
                        negotiating = false;
                        break;
                    }

                    else if (!player.canAfford(property.isMortgaged() ? (int) (offer*0.1) : offer) || offer >= lastOffer || offer < 1) {
                        System.out.print("Do not be outrageous, " + player.getName() + " would never accept that amount. ");
                    } else {
                        goodInt = true;
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Not a number, try again: ");
                }
            }
            goodInt = false; 

            //Accepted? 
            if(boolInput("Private Sale", "The current offer on "+property.getName()+" is "+offer, "Do you accept this price?")) { 
                player.buy(property, offer, this);
                return true;
            } else {
                lastOffer = offer;
            }
        }
        return false;
    }

    /**
    * Displays a dialog to select a player and returns the selected player.
    * @param owner the owner of the property
    * @return the selected player, or null if no selection was made
    */
    Player selectPlayer(Player owner) {
        // Create a list of available players for selection
        List<Player> availablePlayers = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!p.equals(owner)) {
                availablePlayers.add(p);
            }
        } 

        // Create and display the selection dialog
        ChoiceDialog<Player> dialog = new ChoiceDialog<>(availablePlayers.get(0), availablePlayers);
        dialog.setTitle("Select Player");
        dialog.setHeaderText("Select a player to sell the property to");
        dialog.setContentText("Available Players:");

        // Show dialog and capture the selected player
        Optional<Player> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Handles the turn of landing on an unwoned property
     */
    void handleUnownedProperty() { 
        Property property = (Property) current.getLocation();
        
        // If player can afford the property
        if(current.canAfford(property.getPrice())) {
            genLabel.setText(genLabel.getText()+"\nThis property is not owned yet! would you like to buy it? "); 
            if (boolInput("Property", property.getName()+" is not owned yet.", "Would you like to buy it?")) {
                current.buy(property); 
            }
        }
        // If the player has the net worth to afford the property
        else if(current.getNetWorth() >= property.getPrice()) {
            genLabel.setText(genLabel.getText()+"\nThis property is not owned yet! In order to purchase this property though, you will have to sell off assets. Would you like to buy it? ");
            if (boolInput("Property", property.getName()+" is not owned yet.", "Would you like to buy it?")) {
                current.liquidate(property.getPrice(), this);
                current.buy(property); 
            }
        }
        // The player can not afford the property
        else {
            genLabel.setText(genLabel.getText()+"\nThis property is not owned yet! You can not afford this property though, and it will be going up for auction. ");
        }
        if(!current.equals(property.getOwner())) {handleAuction(property);}
    }

    /**
     * Handles game logic for landing on an owned property
     */
    void handleOwnedProperty() { 
        Property property = (Property) current.getLocation();
        Entity owner = property.getOwner();
        
        if (!owner.equals(current)) {
            // Can not afford the rent
            if (!current.canAfford(property.getRent())) {
                genLabel.setText(genLabel.getText()+"\nBreaking! " + current.getName() + " bankrupted by: " + owner.getName() + "! ");
                if(getPlayerCount() == 2) {removePlayer(current); return;}
                current.bankrupted(owner, this);
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                genLabel.setText(genLabel.getText()+"\n"+current.getName() + " landed on " + owner.getName() + "'s property, the rent owed to them is $" + property.getRent() + ".");
            }
        }
        // If we own the property, do nothing
        else {
            genLabel.setText(genLabel.getText()+"\nYou are at " + property.getName() + ", and you own it already.");
        }

        
    }

    /**
     * Handles game logic for landing on any of the special squares
     */
    void handleSpecialSquare() {  
        BoardSpace location = current.getLocation(); 
        Tax tax;
        CardManager cm;
        Go go;
        Jail jail = getJail(); 
        Card card; 

        if (location instanceof Go) {
            go = (Go) location;
            go.reward(current);
            genLabel.setText(genLabel.getText()+"\nCongratulations, " + current.getName() + "! You made it to Go! ");
        } 
        
        else if (location instanceof Jail) {
            jail = (Jail) location;
            if (jail.hasJailed()) {
                genLabel.setText(genLabel.getText()+"\nWelcome to the visitation center, say hello to your friends. ");
            } else {
                genLabel.setText(genLabel.getText()+"\nWelcome to the visitation center. ");
            }
        } 
        
        else if (location instanceof FreeParking) {
            genLabel.setText(genLabel.getText()+"\nWelcome to free parking, take a breather. ");
        } 
        
        else if (location instanceof GoToJail) {  
            jail.addPlayer(current);
            current.setLocation(getJail()); 
            genLabel.setText(genLabel.getText()+"\nGo directly to Jail, do not pass Go, do not collect $200! ");
            if(getDice().doubles()) {incrementTurnIndex();} //Do not go again from doubles if landed on go to jail, re-increment turn index
        } 
        
        else if (location instanceof Tax) {
            tax = (Tax) location;
            //If player can afford the tax pay it
            if(current.canAfford(tax.getTax())) {
                tax.charge(current);
                genLabel.setText(genLabel.getText()+"\nUh oh! You have been charged "+tax.getName()+"! You were charged $" + tax.getTax() + "!");
            } 
            //Liquidate asssets to pay for taxes
            else if(!current.canAfford(tax.getTax()) && current.getNetWorth() >= tax.getTax()) {
                current.liquidate(tax.getTax(), this);
                genLabel.setText(genLabel.getText()+"\nBreaking! " + current.getName() + " can not afford their taxes and goes bankrupt! "); 
            }
            //Player bankrupted, not able ot pay thier taxes
            if(getPlayerCount() == 2) {removePlayer(current);}
            else{current.bankrupted(this);}      
        } 
        
        else if (location instanceof CardManager) {
            cm = (CardManager) location;
            card = cm.draw(this);
            genLabel.setText(genLabel.getText()+"\nWelcome to the "+location.getName()+" square! Your card draw is: \n" + card);
            CardManager.handle(card, this); 
        }
    } 

    /**
     * Handles game logic for landing on a properyt 
     */
    void handleProperty() { 
        Property location = (Property) getCurrentPlayer().getLocation();
        genLabel.setText(genLabel.getText()+"\nYou landed on " + location.getName() + "!"); 

        // If property is not owned, then buy or auction
        if (!(location.getOwner() instanceof Player)) {
            handleUnownedProperty(); 
        }

        // If property is owned by another player, charge rent or bankrupt to player
        else {
            handleOwnedProperty();
        }
    }

    /**
     * Handles the logic of a location a player just landed on
     */
    void handleNewLocation() {
        BoardSpace location = getCurrentPlayer().getLocation();
        //If new location is a property
        if (location instanceof Property) {
            handleProperty();
        }
        // If location is a special square
        else {
            handleSpecialSquare();
        } 
    } 

    /**
     * Handles the turn logic for when the player is in jail
     * @return true for if the player rolled doubles and got out of jail
     */
    boolean handleJail(GridPane primary) {
        boolean freedByDoubles = false;
        String message;
        int choice;

        // Determine choices
        if (current.ownsJailCard()) {
            message = current.canAfford(getBail())
                ? "You are in jail. Choose an option:\n1. Pay fine\n2. Try for doubles\n3. Use 'Get Out of Jail Free' card"
                : "You are in jail. Choose an option:\n2. Try for doubles\n3. Use 'Get Out of Jail Free' card";
        } else {
            message = current.canAfford(getBail())
                ? "You are in jail. Choose an option:\n1. Pay fine\n2. Try for doubles"
                : "You are in jail. You must try for doubles.";
        }

        // Display choice dialog
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, validChoices(current));
        dialog.setTitle("Jail Decision");
        dialog.setHeaderText("You are in jail");
        dialog.setContentText(message);

        Optional<Integer> result = dialog.showAndWait();

        // No action taken
        if (result.isEmpty()) {
            handleJail(primary);
        }

        choice = result.get();

        // Execute choice
        switch (choice) {
            case 1: {
                current.debit(getBail());
                getJail().removePlayer(current);
            }
            case 2: {
                int roll = getDice().roll(current);
                freedByDoubles = getDice().doubles();

                if (freedByDoubles) {
                    getJail().removePlayer(current);
                    current.setLocation(getSpace(10 + roll));
                    handleNewLocation();
                    showAlert("Success", "You rolled doubles (" + roll + ")! You are freed from jail.");
                } else {
                    handleFailedRoll();
                }
            }
            case 3: {
                current.decrementJailCard();
                getJail().removePlayer(current);
                showAlert("Success", "You used a 'Get Out of Jail Free' card!");
            }
        }

        return freedByDoubles;
    }

    //Below are handleJail() helper methods 
    
    /**
     * Gets the valid choice based on the player's current situation for handleJail
     * @param current Player in jail
     * @return ArrayList of choices available to the player
     */
    private ArrayList<Integer> validChoices(Player current) {
        ArrayList<Integer> choices = new ArrayList<>();
        if (current.canAfford(getBail())) choices.add(1);
        choices.add(2);
        if (current.ownsJailCard()) choices.add(3);
        return choices;
    }

    /**
     * Shows alert 
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the lgoc for when a player attempts and fails to roll doubles
     */
    private void handleFailedRoll() {
        current.incrementJailTurns();
        if (current.getJailedTurns() == 3) {
            if (current.ownsJailCard()) {
                current.decrementJailCard();
                getJail().removePlayer(current);
                showAlert("Max Jail Turns", "You used a 'Get Out of Jail Free' card!");
            } else if (current.canAfford(getBail())) {
                current.debit(getBail());
                getJail().removePlayer(current);
                showAlert("Max Jail Turns", "Bail paid and you are released.");
            } else {
                handleBankruptcy();
            }
        } else {
            showAlert("Failed Roll", "You failed to roll doubles. Jail turn incremented.");
        }
    }

    /**
     * Handles bankruptcy caused by jail
     */
    private void handleBankruptcy() {
        if (current.getNetWorth() >= getBail()) {
            if (current.liquidate(getBail(), this)) {
                current.debit(getBail());
                getJail().removePlayer(current);
            } else {
                bankruptPlayer();
            }
        } else {
            bankruptPlayer();
        }
    }

    /**
     * Bankrupts player
     */
    private void bankruptPlayer() {
        showAlert("Bankruptcy", "You cannot afford bail and have been bankrupted.");
        if (getPlayerCount() == 2) {
            removePlayer(current);
        } else {
            current.bankrupted(this);
        }
    }
}