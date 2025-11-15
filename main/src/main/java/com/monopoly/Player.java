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
        super(name); 
        this.piece = piece;
        ID = ID_COUNTER++;
        setInitialLocation(location);
        current = false;
        jailCardNum = 0;
        jail = false;
        jailTurns = 0;  
        
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
     * Moves the player to a new location
     */
    void setLocation(BoardSpace location) {
        this.location = location;
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