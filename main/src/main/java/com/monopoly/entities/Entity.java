/**
 * Rerpresents the banker, holds all banker related functions
 * @author Dale Urquhart
 * @since 2024-10-20
 */

package com.monopoly.entities;

import java.util.ArrayList;

import com.monopoly.boardspaces.Property;

/**
 * Banker object
*/
public class Entity { 

    /**
     * Name of the Banker
    */
    private final String name;

    /**
     * Balance
    */
    private int balance;

    /**
     * Net worth
    */
    private int netWorth;

    /**
     * Banker's properties
    */
    private final ArrayList<Property> properties; 

    /**
     * Starting balance of a player
     */
    private final int STARTING_BAL = 10000;

    /**
     * Player super constructor
    */
    public Entity(String name) {
        this.name = name;
        this.balance = name.equals("Banker") ? Integer.MAX_VALUE : STARTING_BAL;
        netWorth = balance;
        properties = new ArrayList<>();
    } 

    /**
     * Gets name
    * @return name
    */
    public String getName() {
        return name;
    }

    /**
     * Gets balance
    * @return balance
    */
    public int getBalance() {
        return balance;
    }

    /**
     * Gets the total assets of the player
    * @return the total assets
    */
    public int getNetWorth() {
        return netWorth;
    } 
    
    /**
     * Handles complete transaction between two players
     * @param adjustment
     */
    public void pay(Entity payee, int amount) {
        debit(amount);
        payee.credit(amount);  
    }

    /**
     * Adjusts networth of the player
    * @param adjustment the adjustment value of the networth 
    */
    public void adjustNetWorth(int adjustment) { 
        netWorth += adjustment; 
    }

    /**
     * Debits the balance
    * @param adjustment amount to add to balance 
    */
    protected void debit(int adjustment) { 
        balance -= adjustment;
        adjustNetWorth(adjustment * -1); 
    }

    /**
     * Credits the balance
    * @param adjustment to the balance 
    */
    protected void credit(int adjustment) { 
        balance += adjustment;
        adjustNetWorth(adjustment); 
    }

    /**
     * Whether or not an ammount is affordable
    * @param adjustment the amount to check
    * @return true for if it can be afforded
    */
    public boolean canAfford(int adjustment) {
        return getBalance() >= adjustment;
    }

    /**
     * Gets properties owned
    * @return properties owned
    */
    public ArrayList<Property> getProperties() {
        return properties;
    }

    /**
     * Checks if a property's set is fully owned by the player
    * @param check property to check
    * @return true for if the property's set is fully aquired by the player, else false
    */
    public boolean ownsSetFor(Property check) {
        int count = 0;
        for(Property p : getProperties()) { 
            if(p.getType().equals(check.getType())) { 
                count++;
            }
        }
        return count==check.getSetSize();
    } 

    /**
     * Removes a property from the player's possesion
    * @param property the property to remove 
    */
    public void removeProperty(Property property) { 
        properties.remove(property); 
    }

    /**
     * Adds a property to the player's array of properties
    * @param newProperty the property to be added
    */
    public void addProperty(Property newProperty) {
        properties.add(newProperty); 
    } 
}