/**
 * Contatins high level play functions for Monopoly
 * @author Dale Urquhart
 */
 
package com.monopoly;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;

/**
 * High level handler class for Monopoly funcitons
 */
public final class Game {

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
     * Game constructor
     */
    Game(){ 
        turnIndex = 0;  
        players = new ArrayList<> (); 
        players = new ArrayList<>();
        
        List<List<Card>> decks = buildDecks();
        chanceDeck = decks.get(0);
        cCDeck = decks.get(0);  

        map = buildMap(); 
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
        turnIndex = increment(turnIndex);
        current = getPlayers().get(turnIndex);
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
    BoardSpace[] buildMap() {
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
            getGo().reward(current); 
            passedGo = true;
        }
        return passedGo;
    }

    /**
     * Checks is player got doubles or not
     * @return No doubles: 0, Not third doubles: -1, Third doubles: 1
     */
    int handleDoubles() {
        int result;

        if (getDice().doubles()) {
            // If third doubles in a row, go to jail
            if (current.getDoubleCount() == 2) { 
                getJail().addPlayer(current);
                result = 1;
            }
            // If not third doubles in a row, roll again
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
     * Handles the logic of a location a player just landed on
     * @return true for if the location is a purchasable property, or false if the llocatin is a special square
     */
    boolean isProperty() {
        BoardSpace location = getCurrentPlayer().getLocation();
        boolean result;
        
        result = location instanceof Property;  
        
        return result;
    } 
     
    /**
     * Handles game logic for landing on a properyt 
     * If property is not owned, then buy or auction else charge rent
     */
    boolean isOwned() { 
        Property location = (Property) getCurrentPlayer().getLocation(); 
        boolean result = location.getOwner() instanceof Player;
        
        return result;
    }

    /**
     * Handles a roll of the dice
     */
    void handleRoll(GameView view, GameController controller) {   
        view.clearDispPane();
        
        // Make roll and assign the new location
        int roll = getDice().roll(getCurrentPlayer());  
        int newSpace = roll + current.getLocation().getId();

        view.showMessage("You rolled a "+roll+"!");

        // Passed Go
        if(passedGo(newSpace)) {
            view.showMessage("\nYou passed Go! Here is $200.");
            newSpace -= getMap().length;
        }

        // Assign new location
        current.setLocation(getSpace(newSpace));

        // Handle Doubles logic 
        switch (handleDoubles()) {
            case -1:
                view.showMessage("\nYou rolled doubles, you get to roll again after your turn! ");
                break; 
            case 1:
                view.showMessage("\nThat was your third doubles, go to jail! ");
                break; 
            default:
                break;
        }

        // Handle the logic for landing on the new location 
        if(isProperty()) {
            if(isOwned()) {
                controller.handleOwnedProperty();
            } else {
                controller.handleUnownedProperty();
            } 
        } else {
            handleSpecialSquare(controller);
        }

        // Assign next player
        getNextPlayer();
        System.out.println("Got next player: "+current.getName());
        view.displayCurrent(current, controller);

        // Is the next player in jail?
        if(current.inJail()) controller.handleJailTurn();
        else view.showDice();
    }

    /**
     * Handles game logic for landing on any of the special squares
     */
    void handleSpecialSquare(GameController controller) {  
        BoardSpace location = current.getLocation(); 
        Tax tax;
        CardManager cm;
        Go go;
        Jail jail = getJail(); 
        Card card; 

        if (location instanceof Go) {
            go = (Go) location;
            go.reward(current);
            GameView.showAlert("Congratulations, " + current.getName() + "!", "You made it to Go! ");
        } 
        
        else if (location instanceof Jail) {
            jail = (Jail) location;
            if (jail.hasJailed()) {
                GameView.showAlert("Welcome to the visitation center",  "Say hello to your friends. ");
            } else {
                GameView.showAlert("Welcome to the visitation center. ", "Better stay on the right side of these bars...");
            }
        } 
        
        else if (location instanceof FreeParking) {
            GameView.showAlert("Welcome to free parking", "Take a breather. ");
        } 
        
        else if (location instanceof GoToJail) {  
            jail.addPlayer(current);
            current.setLocation(getJail()); 
            GameView.showAlert("Go directly to Jail", "Do not pass Go, do not collect $200! ");
            if(getDice().doubles()) {increment(getTurnIndex());} //Do not go again from doubles if landed on go to jail, re-increment turn index
        } 
        
        else if (location instanceof Tax) {
            tax = (Tax) location;
            //If player can afford the tax pay it
            if(current.canAfford(tax.getTax())) {
                tax.charge(current);
                GameView.showAlert("Uh oh! You have been charged "+tax.getName()+"!", "You were charged $" + tax.getTax() + "!");
            } 
            //Liquidate asssets to pay for taxes
            else if(!current.canAfford(tax.getTax()) && current.getNetWorth() >= tax.getTax()) {
                current.liquidate(tax.getTax(), this);
                GameView.showAlert("Breaking! " + current.getName() + " can not afford their taxes and goes bankrupt!","It was a good run"); 
            }
            //Player bankrupted by bank, not able ot pay thier taxes
            if(getPlayerCount() == 2) {removePlayer(current);}
            else current.bankrupted(Banker.getInstance(), this);      
        } 
        
        else if (location instanceof CardManager) {
            cm = (CardManager) location;
            card = cm.draw(this);
            GameView.showAlert("Welcome to the "+location.getName()+" square! Your card draw is:", card.toString());
            CardManager.handle(card, this, controller); 
        }
    } 

    /**
     * Gets the options a player has for their turn in jail
     * @return Integer list of numbers corrosponding to turn actions
     */
    public List<Integer> getValidJailChoices() {
        List<Integer> choices = new ArrayList<>();
        if (current.canAfford(getBail())) {
            choices.add(1); // Pay fine
        }
        choices.add(2); // Try for doubles
        if (current.ownsJailCard()) {
            choices.add(3); // Use 'Get Out of Jail Free' card
        }
        return choices;
    }

    /**
     * Handle the choice selected from the options in getValidJailChoices()
     * @param choice Choice selected
     * @return Whether or not they were freed by doubles
     */
    public boolean handleJailChoice(int choice) {
        boolean freedByDoubles = false;
        Jail jail = getJail(); 

        switch (choice) {
            case 1: { // Pay fine
                current.debit(getBail());
                jail.removePlayer(current);
            }
            case 2: { // Try for doubles
                int roll = getDice().roll(current);
                freedByDoubles = getDice().doubles();

                if (freedByDoubles) {
                    jail.removePlayer(current);
                    current.setLocation(getSpace(current.getLocation().getId() + roll));
                } else {
                    incrementFailedJailTurn();
                }
            }
            case 3: { // Use 'Get Out of Jail Free' card
                current.decrementJailCard();
                jail.removePlayer(current);
            }
        }

        return freedByDoubles;
    }

    /**
     * 
     */
    private void incrementFailedJailTurn() {
        current.incrementJailTurns();
        if (current.getJailedTurns() == 3) {
            handleMaxJailTurns();
        }
    }

    /**
     * 
     */
    private void handleMaxJailTurns() {
        Jail jail = getJail();
        if (current.ownsJailCard()) {
            current.decrementJailCard();
            jail.removePlayer(current);
        } else if (current.canAfford(getBail())) {
            current.debit(getBail());
            jail.removePlayer(current);
        } else {
            handleBankruptcy();
        }
    }

    /**
     * 
     */
    private void handleBankruptcy() {
        if (current.liquidate(getBail(), this)) {
            current.debit(getBail());
            getJail().removePlayer(current);
        } else {
            bankruptPlayer();
        }
    }

    /**
     * 
     */
    private void bankruptPlayer() {
        if (getPlayerCount() == 2) {
            removePlayer(current);
        } else {
            getCurrentPlayer().bankrupted(Banker.getInstance(), this);
        }
    }
}