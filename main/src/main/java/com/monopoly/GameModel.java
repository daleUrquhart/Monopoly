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

import com.monopoly.boardspaces.BoardSpace;
import com.monopoly.boardspaces.CardSpace;
import com.monopoly.boardspaces.FreeParking;
import com.monopoly.boardspaces.Go;
import com.monopoly.boardspaces.GoToJail;
import com.monopoly.boardspaces.Jail;
import com.monopoly.boardspaces.Property;
import com.monopoly.boardspaces.Railroad;
import com.monopoly.boardspaces.TaxSpace;
import com.monopoly.boardspaces.Utility;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Entity;
import com.monopoly.entities.Player;
import com.monopoly.events.AuctionEvent;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.PaymentEvent;

/**
 * High level handler class for Monopoly funcitons
 * 
 * Strictly contains game state logic and rules
 * Does NOT manipulate game state management, UI updates
 * 
 */
public final class GameModel {

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
    GameModel(){ 
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
    public BoardSpace getSpace(int newSpace) {
        return map[newSpace];
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
    public ArrayList<Player> getPlayers() {
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
    public Player getCurrentPlayer() {
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
     * Gets the game dice
     */
    public Dice getDice() {
        return dice;
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
                        map[index] = Go.getInstance();
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
                        map[index] = Jail.getInstance();
                        break;
                    case "Tax":
                        int taxAmount = Integer.parseInt(action);
                        map[index] = new TaxSpace(name, index, taxAmount);
                        break;
                    case "CardManager":
                        map[index] = new CardSpace(name, index,
                                action.equals("Chance") ? getChanceDeck() : getCommunityChestDeck());
                        break;
                    case "FreeParking":
                        map[index] = FreeParking.getInstance();
                        break;
                    case "GoToJail":
                        map[index] = GoToJail.getInstance();
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
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    public void movePlayerTo(int newSpaceID) {
        if(newSpaceID >= getMap().length) newSpaceID -= getMap().length;
        BoardSpace newSpace = getSpace(newSpaceID);
        current.getLocation().removeOccupant(current);
        current.setLocation(newSpace);
        newSpace.addOccupant(current);
    }

    /**
     * Checks if player passed go nad handles logic for if they do
     * @param newSpace Current location plus roll
     * @return Whether or not the player passsed go
     */
    public boolean passedGo(int newSpace) {  
        return newSpace > getMap().length;
    }

    RollResult rollDice() {
        int roll = getDice().roll(current);  
        int newSpaceID = roll + current.getLocation().getId();  

        // Passed Go
        boolean go = passedGo(newSpaceID);

        // Assign new location
        movePlayerTo(newSpaceID);
        return new RollResult(go, current.getLocation(), roll, getDoublesOutcome());
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
     * Increments space id by given offset and updates position, no Go rewards
     * @param offset numebr of spaces to move by
     */
    public void movePlayerBy(int offset) {
        BoardSpace newSpace = getSpace(offset + current.getLocation().getId());
        current.getLocation().removeOccupant(current);
        current.setLocation(newSpace);
        newSpace.addOccupant(current);
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
     * Increments the turn index
     * @return the new turn index
     */
    int increment(int turnIndex) {  
        turnIndex = turnIndex == (getPlayerCount() - 1) ? 0 : turnIndex + 1; 
        return turnIndex;
    }

    /**
     * Manages the actions for bankrupting given player by the current player 
     * Chance card makes each player pay current
     */
    public BankruptResult processBankruptcy(Player bankrupted, Entity bankrupter) {

        if (bankrupter == null) throw new NullPointerException("Bankrupter cannot be null");

        CompositeEvent auction = new CompositeEvent();
        CompositeEvent transferred = new CompositeEvent();
        List<Property> needDecision = new ArrayList<>();
        List<Property> owned = new ArrayList<>(bankrupted.getProperties());
        removePlayer(bankrupted);

        // Case 1: Banker bankrupts a player, all properties go to auction
        if (bankrupter instanceof Banker) {
            for(Property p : owned) auction.add(new AuctionEvent(p, getPlayers()));
            return new BankruptResult(
                    bankrupted,
                    bankrupter,
                    transferred,
                    needDecision,
                    auction,
                    true,                 
                    getPlayerCount()
            );
        }

        // Case 2: Another player bankrupts this player
        for (Property p : owned) {
            while (p.developed()) p.sellDevelopment();

            // Build list of events to execute
            transferred.add(p.sellTo(bankrupter, 0));

            // If mortgaged, controller will decide
            if (p.isMortgaged()) needDecision.add(p);
        }

        bankrupted.pay(bankrupter, bankrupted.getBalance());

        return new BankruptResult(
                bankrupted,
                bankrupter,
                transferred,
                needDecision,
                auction,
                false,           
                getPlayerCount()
        );
    }
 

    /**
     * Pays a mortgage balance using given player and property
     */
    public GameEvent payMortgage(Player player, Property property) {
        property.unMortgage();   
        return new PaymentEvent(player, Banker.getInstance(), (int) Math.ceil(property.getMortgageValue() * 0.1));
    }

    /**
     * Pays owed interest on amortgaged property
     */
    public GameEvent payMortgageIntrest(Player player, Property property) {
        return new PaymentEvent(player, Banker.getInstance(), (int) Math.ceil(property.getMortgageValue() * 0.1));
    }
 
    /**
     * Handles actions for using a GOOJFC
     */
    public void processJailCard() {
        current.decrementJailCard();
        Jail.getInstance().removePlayer(current); 
        current.resetJailTurns();
        current.flipJailed(); 
    }

    /**
     * Handles actions for paying bail
     */
    public void processBail() {
        Jail jail = Jail.getInstance();
        // new PaymentEvent(current, Banker.getInstance(), jail.getBail()).execute(controller); Should not do it like this, maybe return a game event or smth but id ont think controller should beuse d in the model
        jail.removePlayer(current);
        current.resetJailTurns();
        current.flipJailed(); 
    } 

    public JailOutcome processMaxJailTurns() { 
        Jail jail = Jail.getInstance();

        if (current.ownsJailCard()) {
            processJailCard();
            return JailOutcome.USED_CARD;
        }

        int bail = jail.getBail();

        if (current.canAfford(bail)) {
            processBail();
            return JailOutcome.PAID_BAIL;
        }

        if (current.getNetWorth() >= bail) {
            return JailOutcome.NEEDS_LIQUIDATION;
        }

        return JailOutcome.BANKRUPT;
    }

    public JailOutcome processJailChoice(JailChoice choice) {
        switch(choice) {
            case TRY_FOR_DOUBLES:
                if(processDoubles()) return JailOutcome.FREED_BY_DOUBLES;;
                return JailOutcome.NO_DOUBLES;

            case PAY_BAIL:
                processBail();
                return JailOutcome.PAID_BAIL;

            case USE_CARD:
                processJailCard();
                return JailOutcome.USED_CARD;
            
            default: return null;
        } 
    }

    /**
     * Handles actions for trying for doubles in jail
     */
    Boolean processDoubles() {
        int roll = getDice().roll(current);
        boolean isDoubles = getDice().doubles();
        if (isDoubles) {
            Jail.getInstance().removePlayer(current);
            current.resetJailTurns();
            current.flipJailed(); 
            movePlayerTo(current.getLocation().getId() + roll); 
        } else {
            current.incrementJailTurns();
        }
        return isDoubles;
    }

    public void sendToJail(Player p) { 
        if (getDice().doubles()) {
            increment(getTurnIndex());
        }
        Jail jail = Jail.getInstance();
        movePlayerTo(jail.getId());
        p.flipJailed();
        p.resetDoubleCount(); 
        jail.addPlayer(p);
    }
}