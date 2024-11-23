/**
 * Player creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

/**
 * Player object
 */
final class Player extends Banker {

    /**
     * Path to resources directory
     */
    private static final String PATH = "../resources/com/monopoly/";

    /**
     * Whether or not this player is the current one
     */
    private Boolean current;

    /**
     * Players last roll
     */
    int roll;

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
     * Profile represenitng the player instance
     */
    Profile profile;

    /**
     * Parametrized constructor for Player object
     * @param name Player's name
     */
    Player(String name, BoardSpace location, Profile profile) {
        super(name, 1500);
        this.profile = profile;
        setLocation(location);
        current = false;
        jailCardNum = 0;
        jail = false;
        jailTurns = 0; 
    }

    /**
     * Gets whetehr the player is the current one or not
     * @return true for if this is the current player, false otherwise
     */
    Boolean getCurrent() {
        return current;
    }

    /**
     * Checks double count for turn
     * @return int double count for turn
     */
    int getDoubleCount() {
        return doubleCount;
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
     * Gets teh profile representing this layer instance
     * @return Profile representing this player instance
     */
    Profile getProfile() {
        return profile;
    }

    /**
     * Gets number of get out of jail free cards
     * @return number of get out of jail free cards
     */
    int getJailCardNum() {
        return jailCardNum;
    }

    /**
     * Assigns the Player its profile
     * @param profile
     */
    void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Setter method for player's location by BoardSpace
     * @param newLoc New location of player
     */
    void setLocation(BoardSpace location) {
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
        while(getBalance() < required) {
            game.handleAdvancedTurn();
            System.out.print("End attempt at liquidation to "+required+"? ");
            if(game.boolInput()) {
                game.removePlayer(this);
                return false;
            }
        }
        flipJailed(); 
        return true;
    } 

    /**
     * Handles bankruptcy casued by anotehr player
     * @param bankrupter the player who caused the bankruptcy
     */
    void bankrupted(Banker bankrupter, Game game) {  

        for(Property p : getProperties()) {
            bankrupter.addProperty(p);
            //If property is mortgaged give option to pay it off
            if(p.isMortgaged()) {
                System.out.print(p.toString()+"\nIs mortgaged, would you like to unmortgage it now, or do so later? ");
                if(game.boolInput()) {
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
     * Handles bankruptcy casued by the bank
     */
    void bankrupted(Game game) { 
        game.removePlayer(this); 
        for(Property p : getProperties()) {
            game.handleAuction(p);
        } 
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
    void sell(Property property, Banker banker) { 
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
    void useJailCard() {
        jailCardNum -= 1;
        flipJailed();
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
        String out = "Name: "+getName()+"\nLocation: "+getLocation().getName()+"\nBalance: "+getBalance()+"\nGet out of jail free cards: "+getJailCardNum()+"\nIn Jail? "+inJail()+"\nNet Worth: "+getNetWorth()+"\n---------- Properties ----------\n";
        for(Property p : getProperties()) {
            out += p.toString()+"\n\n";
        }
        return out;
    }
}