/**
 * Player creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly.entities;

import com.monopoly.boardspaces.BoardSpace;
import com.monopoly.boardspaces.Go;
import com.monopoly.boardspaces.Property;

import javafx.scene.image.ImageView;

/**
 * Player object
 */
public final class Player extends Entity { 

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
    public Player(String name, Go location, ImageView piece) {
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
    public int getDoubleCount() {
        return doubleCount;
    }
    
    /**
     * Gets the piece representing the player
     * @return the piece representing the player
     */
    public ImageView getPiece() {
        return piece;
    }

    /**
     * Getst the ID of the player
     * Used for icon corner placement on the map
     * @return ID of the player
     */
    public int getID() {
        return ID;
    }

    /**
     * Gets the total number of houses the player owns
     * @return number of houses player owns
     */
    public int getTotalHouses() {
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
    public int getTotalHotels() {
        int hotels = 0;
        for(Property p : getProperties()) {
            hotels += p.hasHotel() ? 1 : 0;
        }
        return hotels;
    }

    /**
     * Getter method for player's location
     */
    public BoardSpace getLocation() {
        return location;
    }

    /**
     * Gets the player's last roll
     */
    public int getRoll() {
        return roll;
    } 

    /**
     * Gets number of get out of jail free cards
     * @return number of get out of jail free cards
     */
    protected int getJailCardNum() {
        return jailCardNum;
    } 

    /**
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    protected void setInitialLocation(BoardSpace location) { 
        this.location = location;
        location.addOccupant(this);
    } 

    /**
     * Moves the player to a new location
     */
    public void setLocation(BoardSpace location) {
        this.location = location;
    }

    /**
     * Flips current player value
     */
    public void flipCurrent() {
        current = !current; 
    }

    /**
     * Resets the double count for the turn
     */
    public void resetDoubleCount() {
        doubleCount = 0;
    }

    /**
     * Increments double count
     */
    public void incrementDoubleCount() {
        doubleCount += 1;
    }  

    /**
     * Sets the players last roll
     */
    public void setRoll(int roll) {
        this.roll = roll;
    }

    /**
     * Sells a property to the banker (1/2 of purchase price, cannot be mortgaged)
     * @param property the property to be sold
     * @return true for if the action was succesful
     */
    public void sell(Property property) { 
        Banker banker = Banker.getInstance();
        removeProperty(property);
        property.setOwner(banker);
        banker.pay(this, property.getPrice()/2);  
    }

    /**
     * Whether or not the player has a 'Get out of jail free card'
     * @return true for if they do own a 'Get out of jail free card'
     */
    public boolean ownsJailCard() {
        return jailCardNum != 0;
    }

    /**
     * Use a 'Get out of jail free card'
     */
    public void decrementJailCard() {
        jailCardNum -= 1; 
    }

    /**
     * Player aquired a 'Get out of jail free card'
     */
    public void addJailCard() {
        jailCardNum++;
    }

    /**
     * Checks if the player is in jail
     * @return true for if the player is in jail
     */
    public boolean inJail() {
        return jail;
    }

    /**
     * Flips the player's jail status
     */
    public void flipJailed() {
        if(inJail()) {resetJailTurns();}
        jail = !jail;
    }

    /**
     * Resets teh turns spent in jail
     */
    public void resetJailTurns() {
        jailTurns = 0;
    }

    /**
     * gets the amount of turns the player has spent in jail
     */
    public int getJailedTurns() {
        return jailTurns;
    }

    /**
     * Increments the amount of turns spent in jail
     */
    public void incrementJailTurns() {
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