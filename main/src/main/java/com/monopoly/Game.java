/**
 * Contatins high level play functions for Monopoly
 * @author Dale Urquhart
 */
 
package com.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * High level handler class for Monopoly funcitons
 * 
 * Strictly contains game state logic and rules
 * Does NOT manipulate game state management, UI updates
 * 
 */
public final class Game {

    /**
     * Path to resources
     */
    private static final String PATH = "/com/monopoly/";

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
    private final ArrayList<Player> players;

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
     * Game constructor
     * Builds Chance and CC decks then builds game map
     */
    Game(){ 
        turnIndex = 0;   
        players = new ArrayList<>();
        
        System.out.println("\tInitiating buildDecks()...");
        List<List<Card>> decks = buildDecks();
        System.out.println("\tGame decks built...");
        chanceDeck = decks.get(0);
        cCDeck = decks.get(1);  

        System.out.println("\tInitiating buildMap()...");
        map = buildMap();
        System.out.println("\tGame map built");
    }

    /**
     * Gets the space index from map
     * @param index
     */
    BoardSpace getSpace(int newSpace) {
        return map[newSpace];
    } 

    /**
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    void movePlayerTo(Player p, int newSpaceID) {
        if(passedGo(newSpaceID)) newSpaceID -= getMap().length;
        BoardSpace newSpace = getSpace(newSpaceID);
        p.getLocation().removeOccupant(p);
        p.setLocation(newSpace);
        newSpace.addOccupant(p);
    }

    /**
     * Gets the board map
     * @return the board map
     */
    BoardSpace[] getMap() {
        return map;
    } 

    /**
     * Gets the chance deck
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
     * Gets the Go space
     */
    Go getGo() {
        return (Go) getSpace(0);
    }

    /**
     * Gets the next player and increments turn index
     */
    void advanceTurn() {  
        getCurrentPlayer().flipCurrent();
        turnIndex = increment(turnIndex);
        current = getPlayers().get(turnIndex);
        current.flipCurrent();
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
            List<Card> chance = Card.getChanceDeck(getClass().getResourceAsStream(PATH + "cards.csv"));
            decks.add(chance);
            System.out.println("\t\tChance deck built");

            decks.add(Card.getCCDeck(getClass().getResourceAsStream(PATH + "cards.csv")));
            System.out.println("\t\tCC deck built");

            return decks;
        } catch (IOException e) {
            System.out.println("Error opening game data CSVs in deck building.");
            return null;
        }
    }
 
    /**
     * Builds the game map
     */
    BoardSpace[] buildMap() {
        map = new BoardSpace[40]; 
        try (InputStream in = getClass().getResourceAsStream(PATH + "properties.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(in));) { 
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
            System.out.println("Error opening game map CSV"); 
        } catch (Exception e) {
            System.out.println("Uncaught exception in building game map");
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
        if(p.equals(current)) setCurrentPlayer(getPlayers().get(0));
    }

    /**
     * Adds a player to ArrayList<Player> players
     * @param p player to add to players
     */
    void addPlayer(Player p) {
        // Set first player added to be the current player
        if(players.isEmpty()) {
            setCurrentPlayer(p);
        }
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
    int increment(int turnIndex) {  
        turnIndex = turnIndex == (getPlayerCount() - 1) ? 0 : turnIndex + 1; 
        return turnIndex;
    }

    /**
     * Checks if player passed go nad handles logic for if they do
     * @param newSpace Current location plus roll
     * @return Whether or not the player passsed go
     */
    boolean passedGo(int newSpace) { 
        boolean passedGo = false;
        if(newSpace >= getMap().length) { 
            passedGo = true;
        }
        return passedGo;
    }

    /**
     * Checks is player got doubles or not
     * @return No doubles: 0, Not third doubles: -1, Third doubles: 1
     */
    int getDoublesOutcome() {
        int result;

        if (getDice().doubles()) { 
            if (current.getDoubleCount() == 2) {  
                sendToJail(current);
                result = 1;
            } 
            else { 
                current.incrementDoubleCount();
                decrementTurnIndex();
                result = -1;
            }
        } else { 
            current.resetDoubleCount();
            result = 0;
        }

        return result;
    }

    /**
     * Handles actions for using a GOOJFC
     */
    void processJailCard() {
            current.decrementJailCard();
            getJail().removePlayer(current); 
            current.resetJailTurns();
            current.flipJailed(); 
    }

    /**
     * Handles actions for paying bail
     */
    void processBail() {
            current.pay(Banker.getInstance(), getJail().getBail());
            getJail().removePlayer(current);
            current.resetJailTurns();
            current.flipJailed(); 
    }

    /**
     * Handles actions for trying for doubles in jail
     */
    Boolean processDoubles() {
        int roll = getDice().roll(current);
        boolean isDoubles = getDice().doubles();
        if (isDoubles) {
            getJail().removePlayer(current);
            current.resetJailTurns();
            current.flipJailed(); 
            movePlayerTo(current, current.getLocation().getId() + roll); 
        } else {
            current.incrementJailTurns();
        }
        return isDoubles;
    }

    void sendToJail(Player p) { 
        movePlayerTo(p, getJail().getId());
        p.flipJailed();
        p.resetDoubleCount(); 
        getJail().addPlayer(p);
    }

    /**
     * Manages the actions for bankrupting given player by the current player 
     * Chance card makes each player pay current
     */
    void bankruptPlayer(Player bankrupted, Entity bankrupter, GameController controller) {
        removePlayer(current);
        if (getPlayerCount() == 1) controller.handleWinner();
        //else bankrupted.bankrupted(bankrupter, this, controller);
        // Had to address possible null pointer of bankrupter
        else{ 
            if(bankrupter == null) throw new NullPointerException("Bankrupter is null");

            if(bankrupter instanceof Banker) {
                for (Property p : bankrupted.getProperties()) {
                    controller.handleAuction(p);
                }
            }
            
            else {
                for(Property p : bankrupted.getProperties()) {
                    while(p.developed()) p.sellDevelopment();
                    bankrupter.addProperty(p);
                    //If property is mortgaged give option to pay it off
                    if(p.isMortgaged()) {
                        controller.handleMortgagedPurchase((Player) bankrupter, p); 
                    }    
                }  
            }
            
            bankrupted.pay(bankrupter, bankrupted.getBalance()); 
        }
    }  

    /**
     * Pays a mortgage balance using given player and property
     */
    void payMortgage(Player player, Property property) {
        player.pay(Banker.getInstance(), (int) Math.ceil(property.getMortgageValue() * 1.1));
        property.unMortgage();   
    }

    /**
     * Pays owed interest on amortgaged property
     */
    void payMortgageIntrest(Player player, Property property) {
        player.pay(Banker.getInstance(), (int) Math.ceil(property.getMortgageValue() * 0.1));
    }
}