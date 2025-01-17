/**
 * Player creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import javafx.scene.image.ImageView;

/**
 * Player object
 */
final class Player extends Entity { 

    /**
     * ID Counter
     */
    private static int ID_COUNTER = 0;

    /**
     * ID of the player instance
     */
    private final int ID;

    /**
     * Whether or not this player is the current one
     */
    private boolean current;

    /**
     * Players last roll
     */
    private int roll;

    /**
     * Player's location on the board
     */
    private BoardSpace location;

    /**
     * Number of 'Get out of jail free'cards the player owns
     */
    private int jailCardNum;

    /**
     * Player's jailed status
     */
    private boolean jail;

    /**
     * Number of turns the player has spent in jail consecutively
     */
    private int jailTurns; 

    /**
     * Amount of times doubles have been rolled in a row for one turn
     */
    private int doubleCount; 

    /**
     * Piece representing the player
     */
    private final ImageView piece;

    /**
     * Parametrized constructor for Player object
     * @param name Player's name
     */
    Player(String name, Go location, ImageView piece) { 
        super(name, 1500); 
        this.piece = piece;
        setInitialLocation(location);
        current = false;
        jailCardNum = 0;
        jail = false;
        jailTurns = 0;  
        ID = ID_COUNTER++;
    } 

    /**
     * Checks double count for turn
     * @return int double count for turn
     */
    int getDoubleCount() {
        return doubleCount;
    }
    
    /**
     * Gets the piece representing the player
     * @return the piece representing the player
     */
    ImageView getPiece() {
        return piece;
    }

    /**
     * Getst the ID of the player
     * Used for icon corner placement on the map
     * @return ID of the player
     */
    int getID() {
        return ID;
    }

    /**
     * Gets the total number of houses the player owns
     * @return number of houses player owns
     */
    int getTotalHouses() {
        int houses = 0;
        for(Property p : getProperties()) {
            houses += p.getHouses();
        }
        return houses;
    }

    /**
     * Gets the total number of hotels the player owns
     * @return number of hotels the player owns
     */
    int getTotalHotels() {
        int hotels = 0;
        for(Property p : getProperties()) {
            hotels += p.hasHotel() ? 1 : 0;
        }
        return hotels;
    }

    /**
     * Getter method for player's location
     */
    BoardSpace getLocation() {
        return location;
    }

    /**
     * Gets the player's last roll
     */
    int getRoll() {
        return roll;
    } 

    /**
     * Gets number of get out of jail free cards
     * @return number of get out of jail free cards
     */
    int getJailCardNum() {
        return jailCardNum;
    } 

    /**
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    void setInitialLocation(BoardSpace location) { 
        this.location = location;
        location.addOccupant(this);
    }
    
    /**
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    void setLocation(BoardSpace location) {
        location.removeOccupant(this);
        this.location = location;
        location.addOccupant(this);
    }

    /**
     * Flips current player value
     */
    void flipCurrent() {
        current = !current; 
    }

    /**
     * Resets the double count for the turn
     */
    void resetDoubleCount() {
        doubleCount = 0;
    }

    /**
     * Increments double count
     */
    void incrementDoubleCount() {
        doubleCount += 1;
    }

    /**
     * Liquidates assets to achieve a set balance
     * @param required balance
     */
    boolean liquidate(int required, Game game) { 
        return true;
    } 

    /**
     * Handles bankruptcy casued by anotehr player
     * @param bankrupter the player who caused the bankruptcy
     */
    void bankrupted(Entity bankrupter, Game game) {  

        for(Property p : getProperties()) {
            bankrupter.addProperty(p);
            //If property is mortgaged give option to pay it off
            if(p.isMortgaged()) {
                System.out.print(p.toString()+"\nIs mortgaged, would you like to unmortgage it now, or do so later? ");
                if(GameView.getBoolInput("Bankruptcy", p.toString()+" is mortgaged.", "Would you like to unmortgage it now, for "+(int) ((double) p.getMortgageValue() * 1.1)+", or wait until later and only pay the current intrest owing of "+(int) ((double) p.getMortgageValue() * 0.1)+".")) {
                    p.unMortgage();
                } else {
                    System.out.println("Property remains mortgaged, intrest only payment made. ");
                    bankrupter.debit((int) ((double) p.getMortgageValue() * 0.1));
                }
            } 
            //Sell any developments back to the bank, balance goes to bankrupted player and is transfered over at the bottom of method along with balance at bankruptcy
            else if(p.developed()) {
                p.sellDevelopment();
            }
        }

        bankrupter.credit(getBalance());
        game.removePlayer(this);
    } 

    /**
     * Sets the players last roll
     */
    void setRoll(int roll) {
        this.roll = roll;
    }

    /**
     * Sells a property to the banker (1/2 of purchase price, cannot be mortgaged)
     * @param property the property to be sold
     * @return true for if the action was succesful
     */
    void sell(Property property) { 
        Banker banker = Banker.getInstance();
        removeProperty(property);
        property.setOwner(banker);
        credit((int) (property.getPrice() / 2));
        banker.debit((int) (property.getPrice() / 2)); 
    }

    /**
     * Whether or not the player has a 'Get out of jail free card'
     * @return true for if they do own a 'Get out of jail free card'
     */
    boolean ownsJailCard() {
        return jailCardNum != 0;
    }

    /**
     * Use a 'Get out of jail free card'
     */
    void decrementJailCard() {
        jailCardNum -= 1; 
    }

    /**
     * Player aquired a 'Get out of jail free card'
     */
    void addJailCard() {
        jailCardNum++;
    }

    /**
     * Checks if the player is in jail
     * @return true for if the player is in jail
     */
    boolean inJail() {
        return jail;
    }

    /**
     * Flips the player's jail status
     */
    void flipJailed() {
        if(inJail()) {resetJailTurns();}
        jail = !jail;
    }

    /**
     * Resets teh turns spent in jail
     */
    void resetJailTurns() {
        jailTurns = 0;
    }

    /**
     * gets the amount of turns the player has spent in jail
     */
    int getJailedTurns() {
        return jailTurns;
    }

    /**
     * Increments the amount of turns spent in jail
     */
    void incrementJailTurns() {
        jailTurns += 1;
    }

    /**
     * Handles the purchase of a new proerty for the plaeyr bought by landing property added to players properties in setOwner
     * @param newProperty the property to buy
     * @return whetehr or not the player could afford the purchase
     */
    void buy(Property newProperty) { 
        debit(newProperty.getPrice());
        newProperty.setOwner(this);  
    } 

    @Override
    public String toString() {
        String out = "Name: "+getName()+"\nLocation: "+getLocation().getName()+"\nBalance: "+getBalance()+"\nGet out of jail free cards: "+getJailCardNum()+"\nIn Jail? "+inJail()+"\nNet Worth: "+getNetWorth();//+"\n---------- Properties ----------\n";
        /*
        for(Property p : getProperties()) {
            out += p.toString()+"\n\n";
        }
        */
        return out;
    }
}