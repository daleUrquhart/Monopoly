/**
 * Contatins high level play functions for Monopoly
 */


package com.monopoly;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.Scanner;
import java.util.InputMismatchException; 

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
     * Represents the banker
     */
    private final Banker banker;

    /**
     * Represents the maximum number of players allowed to play in a game
     */
    private final int MAX_PLAYERS = 4;

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
     * Game Scanner
     */
    private final Scanner sc;

    /**
     * Dice for the game (represents two dice rolled together)
     */
    private final Dice dice;

    /**
     * Community Chest deck
     */
    private final ArrayList<Card> cCDeck;

    /**
     * Chance Deck
     */
    private final ArrayList<Card> chanceDeck;

    /**
     * Game constructor
     */
    Game(){
        sc = new Scanner(System.in); 
        turnIndex = -1; 
        players = new ArrayList<> ();
        banker = new Banker();
        
        chanceDeck = buildChanceDeck();
        cCDeck = buildCCDeck(); 
        System.out.println("\tDecks built succesfully..."); 
        
        setPlayers();
        System.out.println("\tPlayers built succesfully");

        map = buildMap();
        System.out.println("\tMap built succesfully...");

        dice = new Dice(getPlayers());
        System.out.println("\tDice built succesfully...");
         
        System.out.println("Game created succefully");
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
    ArrayList<Card> getChanceDeck() {
        return chanceDeck;
    }

    /**
     * Gets the community chest deck
     */
    ArrayList<Card> getCommunityChestDeck() {
        return cCDeck;
    }

    /**
     * Gets the banker
     */
    public Banker getBanker() {
        return banker;
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
        if(getTurnIndex() != -1) {
            return getPlayer(getTurnIndex());
        }
        return null;
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
        incrementTurnIndex(); 
        getCurrentPlayer().flipCurrent();
        return getCurrentPlayer();
    }

    /**
     * Gets the game dice
     */
    Dice getDice() {
        return dice;
    } 

    /**
     * Builds Community Chest Deck
     */
    ArrayList<Card> buildCCDeck() {
        try{return Card.getCCDeck(PATH+"cards.csv");} catch(IOException e) {System.out.println("Come on, idiot. Give me a good csv"); return new ArrayList<Card>();}
    }

    /**
     * Builds Chance Deck
     */
    ArrayList<Card> buildChanceDeck() {
        try{return Card.getChanceDeck(PATH+"cards.csv");} catch(IOException e) {System.out.println("Come on, idiot. Give me a good csv"); return new ArrayList<Card>();}
    }

    /**
     * Builds the game map
     */
    public BoardSpace[] buildMap() {
        map = new BoardSpace[40]; 
        try (BufferedReader br = new BufferedReader(new FileReader(PATH+"properties.csv"))) { 
            String line;
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                //System.out.println(line);
                int index = Integer.parseInt(values[0]);
                String type = values[1];
                String name = values[2];
                String group = values[3];
                int price = Integer.parseInt(values[4]);
                String rentStructure = values[5];
                String action = values[6];
    
                switch (type) {
                    case "Go":
                        map[index] = new Go(banker);
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
                        map[index] = new Property(banker, banker, name, group, 0, index, rentArray[0], rentArray[1], rentArray[2],
                                rentArray[3], rentArray[4], rentArray[5], price / 2, 50, price);
                        break;
                    case "Railroad":
                        map[index] = new Railroad(name, index, price, banker, banker);
                        break;
                    case "Utility": 
                        map[index] = new Utility(name, index, price, banker, banker, players);
                        break;
                    case "Jail":
                        map[index] = new Jail(players);
                        break;
                    case "Tax":
                        int taxAmount = Integer.parseInt(action);
                        map[index] = new Tax(name, index, taxAmount, banker);
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
     * Handles taking the input to recieve the numebr of players to play in the game
     *
     * @return Number of players to play in the game
     */
    void setPlayers() {
        boolean valid = false;

        System.out.print("Welcome, how many players will you have? ");

        //Get player count
        while(!valid) {
            try {
                playerCount = sc.nextInt();
                if (playerCount > 1 && MAX_PLAYERS >= playerCount) {
                    valid = true;
                } else {
                    System.out.print("Bad input, max player amount is 4 and yyou need at least 2, try again. ");
                }
            } catch (InputMismatchException e) {
                System.out.print("That is not a valid input.. Try again. ");
                sc.nextLine();
            }
        }
        sc.nextLine();

        players = new ArrayList<>();
        String name;
        ArrayList<String> names = new ArrayList<>();

        //Get names
        for(int i = 0; i < playerCount; i++) {
            System.out.printf("Enter the name for player %d: ", i+1);
            name = sc.nextLine();
            while(names.contains(name)) {
                System.out.print("Someone already has that name. Try again: ");
                name = sc.nextLine();
            }
            names.add(name);
            players.add(new Player(name)); 
            if(i==0) {players.get(i).flipCurrent();}
        } 
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
     * Decrements the turn index and will index the previously played player
     */
    void decrementTurnIndex() {
        turnIndex -= 1;
        if(getTurnIndex() == -1) {
            turnIndex = getPlayerCount()-1;
        }
    }

    /**
     * Increments the turn index
     */
    void incrementTurnIndex() {
        turnIndex = getTurnIndex() == getPlayerCount() - 1 ? 0 : turnIndex + 1;

    }

    /**
     * Handles error checking on 'yes' or 'no' inputs
     * @return true for if teh response is 'yes'
     */
    boolean boolInput() {
        String input=""; boolean badInput = true;
        while(badInput) {
            input = sc.nextLine();
            if(input.equals("yes") || input.equals("no")) {
                badInput = false;
            } else {
                System.out.print("\tThe input may only be 'yes' or 'no': ");
            }
        }
        return input.equals("yes");
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
        Banker highestBidder = location.getOwner(); 

        //Per player:
        while(open) {
            yes = false;

            //Do you want to bid?
            if(bid < bidder.getBalance() && !bidder.equals(location.getOwner())) {
                System.out.print(bidder.getName() + ", would you like to bid on " + location.getName() + "? The current bid is at $" + bid + ". ");
                yes = boolInput();
            }

            //Yes?
            if(yes) {
                passedTurns = 0; goodInt = false;
                System.out.print("Enter your bid: $");
                //Validate integer input
                while(!goodInt) {
                    try {
                        attempt = sc.nextInt();
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
                    sc.nextLine();
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
            System.out.println("Bidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
            highestBidder.buy(location, bid, this);
            return true;
        }
        //Player auctioning property off to other players
        else if(highestBidder instanceof Player && location.getOwner() instanceof Player) {
            System.out.print("The highest bid was "+bid+" do you want to accept that amount, "+location.getOwner().getName()+"? ");
            if(boolInput()) {
                System.out.println("Bidding has concluded, "+highestBidder.getName()+" has won the property "+location.getName()+" with a bid of $"+bid+".");
                highestBidder.buy(location, bid, this);
                return true;
            } else {
                System.out.println("Owner disatisfied with acution, recants property. ");
                return false;
            }
        }
        //Property stays with the bank
        else {
            System.out.println("No bids made, "+location.getName()+" stays with "+location.getOwner().getName()+". ");
            return false;
        }
    } 

    /**
     * Handles dice rolling and player movement logic
     * @param current player
     */
    boolean handleRoll() {
        Player current = getCurrentPlayer();
        int roll = getDice().roll();
        int newSpace = roll + current.getLocation().getId();
        boolean doubles = false;

        System.out.println("You rolled a "+roll+". ");

        if(newSpace >= getMap().length) {
            newSpace -= getMap().length;

            getGo().reward(getPlayer(getTurnIndex()));
            System.out.println("Congratulations, you made it another trip around the sun! Here is $200. ");
        }
        current.setLocation(getSpace(newSpace));

        //Got doubles
        if (getDice().doubles()) {
            //If third doubles in a row, go to jail
            if (current.getDoubleCount() == 2) {
                System.out.println("That was your third doubles, go to jail! ");
                current.flipJailed();
                current.resetDoubleCount();
            }
            //If not third doubles in a row, roll again
            else {
                System.out.println("You rolled doubles, you get to roll again after your turn! ");
                doubles = true;
                current.incrementDoubleCount();
            }
        }
        //Did not get doubles
        else {
            current.resetDoubleCount();
        }
        return doubles;
    }

    /**
     * Handles special logic for when player is in jail
     * @param current player (in jail)
     * @return true for if player decided to try to roll for doubles and was succesful and now a proeprty needs to be handled
     */
    boolean handleJail() {
        boolean doubles = false, badInt = true, hasChoice = true;
        String message;
        Player current = getCurrentPlayer(); 
        int choice = 2, roll;

        //Get choice
        if (current.ownsJailCard()) {
            message = current.canAfford(getBail()) ? "You are in jail, would you like to pay your fine, try your luck at doubles, or use a get out of jail free card (1, 2, or 3)? " : "You can not afford bail, you must try to roll for doubles, or use your get out of jail free card (2, or 3). ";
        } else {
            message = current.canAfford(getBail()) ? "You are in jail, would you like to pay your fine, try your luck at doubles (1, or 2)? " : "You can not afford bail, you must try to roll for doubles. ";
            hasChoice = current.canAfford(getBail());
        }
        System.out.print(message);

        //Validate choice
        while (badInt && hasChoice) {
            try {
                choice = sc.nextInt();
                if (choice == 1 || choice == 2 || (choice == 3 && current.ownsJailCard())) {
                    badInt = false;
                } else {
                    System.out.print("\tNot an option, try again.. ");
                }
            } catch (InputMismatchException e) {
                System.out.print("\tNot an option, try again. ");
            }
            sc.nextLine();
        } 

        //Execute choice
        switch (choice) {
            case 1:
                current.debit(getBail());
                current.flipJailed();
                break;

            case 2:
                roll = getDice().roll();
                doubles = getDice().doubles();

                if (doubles) {
                    current.flipJailed(); 
                    current.setLocation(getSpace(10 + roll));
                    System.out.println("You rolled doubles (" + roll + ")! You are freed from jail. ");
                } 
                
                else {
                    System.out.println("You failed to roll doubles. ");
                    //Maximum amount of jail turns reached, force bail payment
                    if (current.getJailedTurns() == 2) {
                        System.out.print("Maximum jail turns reached. ");
                        if(current.ownsJailCard()) {
                            System.out.println("Get out of jail free card used. ");
                            current.useJailCard();
                        }
                        //If player can afford bail, roll and move
                        else if (current.canAfford(getBail())) {
                            System.out.println("Bail charged. ");
                            current.debit(getBail());
                            current.flipJailed();
                        }
                        //If player can not afford bail, bankrupted by the bank
                        else {
                            //Net worth is greater or equal to than bail
                            if (current.getNetWorth() >= getBail()) {
                                System.out.println("You must forefit assets to pay bail. ");
                                if(current.liquidate(getBail(), this)) {current.debit(getBail()); break;}
                            }
                            System.out.println("You have been bankrupted by not being able to afford your bail. ");
                            if(getPlayerCount() == 2) {removePlayer(current); break;}
                            current.bankrupted(this);
                        }
                    }
                    //Max amount of jail turns not reaached
                    else {
                        current.incrementJailTurns();
                    }
                }

                break;

            case 3:
                System.out.println("Get out of jail free card used. ");
                current.useJailCard(); 
        } 
        return doubles;
    }

    /**
     * Handles the private sale of a property
     * @return true for if there was a succesful transfer of property
     */
    boolean handlePrivateSale(Property property) {
        Player player = null;
        boolean negotiating=true, goodInt=false;
        int lastOffer = Integer.MAX_VALUE, offer=0;

        //Prompt input
        String name;
        ArrayList<String> names = new ArrayList<>();
        
        System.out.print("Who would you like to sell the property to? (");
        for(Player p : getPlayers()) {
            if(p.equals(property.getOwner())) {continue;}
            names.add(p.getName());
            System.out.print(" "+p.getName()+" ");
        }
        System.out.print("): ");

        //Get name
        name = sc.nextLine();
        while(!names.contains(name)) {
            System.out.print("Name not found, try again: ");
            name = sc.nextLine();
        }

        //Get Player
        for(Player p : getPlayers()) {
            if(p.getName().equals(name)) {
                player = p;
            }
        }

        //Get price
        while(negotiating) {
            //Get seller offer
            System.out.print(property.getOwner().getName() + ", what do you offer? (offer -1 to quit negotiations) ");
            while(!goodInt) {
                try {
                    offer = sc.nextInt();
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
            sc.nextLine();

            //Accepted?
            System.out.print(player.getName()+", do you accept? ");
            if(boolInput()) { 
                player.buy(property, offer, this);
                return true;
            } else {
                lastOffer = offer;
            }
        }
        return false;
    }

    /**
     * Handles advanced turn action (player wishes to have info displayd), and prompts purchase of proeprty
     */
    void handleAdvancedTurn() {
        Player player = getCurrentPlayer();
        boolean result;
        System.out.println(player+"\n");

        System.out.println("Would you like to view your advanced turn options? ");
        result = boolInput();
        if(!result) {return;}

        //Advanced turn logic
        //For property owned
        for(Property p : player.getProperties()) {
            System.out.print("Would you like to take an action on "+p.getName()+"? ");
            if(!boolInput()) {continue;}

            //Un-Mortgage the property?
            if(p.isMortgaged()) {
                if(player.canAfford(p.getMortgageValue())) {
                    System.out.print("Would you like to un-mortgage "+p.getName()+" for $"+p.getMortgageValue()+"? ");
                    if(boolInput()) {
                        p.unMortgage();
                    }
                }
            }
            //Mortgage the property?
            else {
                System.out.print("Would you like to mortgage "+p.getName()+" for $"+p.getMortgageValue()+"? ");
                if(boolInput()) {p.mortgage();continue;}
            }
            //Sell to another player? Auction or private sale?
            System.out.print("Would you like to sell this property to another player? ");
            if(boolInput()) {
                System.out.print("Would you like to auction the property (private sale otherwise)? ");
                if(boolInput()) {
                    //Auction
                    if(handleAuction(p)) {continue;}
                }
                else {
                    //Private Sale
                    if(handlePrivateSale(p)) {continue;}
                }
            }
            //Sell property to bank?
            if(!p.setDeveloped() && !p.isMortgaged()) {
                System.out.print("Sell "+p.getName()+" to the bank? ");
                if(boolInput()) {
                    player.sell(p, getBanker());
                }
            }
            //sell developments back to the bank, the else works because it can not have developments and be mortgaged
            else {
                if(p.developed()) {
                    System.out.print("You have "+(p.hasHotel() ? "a hotel" : p.getHouses()+" houses")+", would you like to sell a development for $"+p.getDevelopmentCost()+"? ");
                    if(boolInput()) {
                        p.sellDevelopment();
                    }
                }
            }
            //Buy development?
            if(player.ownsSetFor(p) && !p.hasHotel() && player.canAfford(p.getDevelopmentCost())) {
                System.out.print("Would you like to buy a "+(p.getHouses() == 4 ? "hotel" : "house")+" for "+p.getName()+"? ");
                if(boolInput()) {
                    p.buyDevelopment();
                }
            }
        }
        System.out.println("");
    }

    /**
     * Handles the turn of landing on an unwoned property
     */
    void handleUnownedProperty() {
        Player current = getCurrentPlayer();
        Property property = (Property) current.getLocation();
        
        // If player can afford the property
        if(current.canAfford(property.getPrice())) {
            System.out.print("This property is not owned yet! would you like to buy it? "); 
            if (boolInput()) {
                current.buy(property); 
            }
        }
        // If the player has the net worth to afford the property
        else if(current.getNetWorth() >= property.getPrice()) {
            System.out.print("This property is not owned yet! In order to purchase this property though, you will have to sell off assets. Would you like to buy it? ");
            if (boolInput()) {
                current.liquidate(property.getPrice(), this);
                current.buy(property); 
            }
        }
        // The player can not afford the property
        else {
            System.out.println("This property is not owned yet! You can not afford this property though, and it will be going up for auction. ");
        }
        if(!current.equals(property.getOwner())) {handleAuction(property);}
    }

    /**
     * Handles game logic for landing on an owned property
     */
    void handleOwnedProperty() {
        Player current = getCurrentPlayer();
        Property property = (Property) current.getLocation();
        Banker owner = property.getOwner();
        
        if (!owner.equals(current)) {
            // Can not afford the rent
            if (!current.canAfford(property.getRent())) {
                System.out.println("Breaking! " + current.getName() + " bankrupted by: " + owner.getName() + "! ");
                if(getPlayerCount() == 2) {removePlayer(current); return;}
                current.bankrupted(owner, this);
            }
            // Can afford the rent
            else {
                property.chargeRent(current);
                System.out.println(current.getName() + " landed on " + owner.getName() + "'s property, the rent owed to them is $" + property.getRent() + ".");
            }
        }
        // If we own the property, do nothing
        else {
            System.out.println("You are at " + property.getName() + ", and you own it already.");
        }

        
    }

    /**
     * Handles game logic for landing on any of the special squares
     */
    void handleSpecialSquare() { 
        Player current = getCurrentPlayer();
        BoardSpace location = current.getLocation(); 
        Tax tax;
        CardManager cm;
        Go go;
        Jail jail;
        GoToJail gtj;
        Card card; 

        if (location instanceof Go) {
            go = (Go) location;
            go.reward(current);
            System.out.println("Congratulations, " + current.getName() + "! You made it to Go! ");
        } 
        
        else if (location instanceof Jail) {
            jail = (Jail) location;
            if (jail.hasJailed()) {
                System.out.println("Welcome to the visitation center, say hello to your friends. ");
            } else {
                System.out.println("Welcome to the visitation center. ");
            }
        } 
        
        else if (location instanceof FreeParking) {
            System.out.println("Welcome to free parking, take a breather. ");
        } 
        
        else if (location instanceof GoToJail) {
            gtj = (GoToJail) location;
            gtj.jail(current);
            System.out.println("Go directly to Jail, do not pass Go, do not collect $200! ");
            if(getDice().doubles()) {incrementTurnIndex();} //Do not go again from doubles if landed on go to jail, re-increment turn index
        } 
        
        else if (location instanceof Tax) {
            tax = (Tax) location;
            //If player can afford the tax pay it
            if(current.canAfford(tax.getTax())) {
                tax.charge(current);
                System.out.println("Uh oh! You have been charged "+tax.getName()+"! You were charged $" + tax.getTax() + "!");
            } 
            //Liquidate asssets to pay for taxes
            else if(!current.canAfford(tax.getTax()) && current.getNetWorth() >= tax.getTax()) {
                current.liquidate(tax.getTax(), this);
                System.out.println("Breaking! " + current.getName() + " can not afford their taxes and goes bankrupt! "); 
            }
            //Player bankrupted, not able ot pay thier taxes
            if(getPlayerCount() == 2) {removePlayer(current); return;}
            else{current.bankrupted(this);}      
        } 
        
        else if (location instanceof CardManager) {
            cm = (CardManager) location;
            card = cm.draw(this);
            System.out.println("Welcome to the "+location.getName()+" square! Your card draw is: \n" + card);
            CardManager.handle(card, this); 
        }
    } 

    /**
     * Handles game logic for landing on a properyt 
     */
    void handleProperty() { 
        Property location = (Property) getCurrentPlayer().getLocation();
        System.out.println("You landed on " + location.getName() + "!"); 

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
     * Handles player's turn from dice roll to incrementing to the next turn
     * Main game loop
     */
    void gameLoop() {
        Player current;
        boolean winner = false, doubles = false; 

        while (!winner) {
            //Assign current player
            if(getPlayerCount() == 1) {winner = true; continue; }
            if(doubles) {
                doubles = false;
                decrementTurnIndex();
            }
            current = getNextPlayer();  
            if(current == null) {winner = true; continue; } 
            System.out.println("\n"+current.getName()+"'s Turn ");
            
            /*
            //See stats?
            System.out.print("\n" + current.getName() + "'s turn. Would you like to view your player's data? ");
            if(boolInput()){handleAdvancedTurn();}
            */

            //Player is in jail
            if (current.inJail()) {
                //If they did not roll doubles
                if (!handleJail()) {
                    continue;
                }
            }
            //Player is not in jail
            else {
                doubles = handleRoll();
                if(current.inJail()) { // Rolled their third double in a row and got sent to jail, kills turn
                    continue;
                } 
            }
 
            handleNewLocation();
        }
        System.out.println(getCurrentPlayer().getName()+" wins the game!");
    }
}