/**
 * Utility location for the board
 */

package com.monopoly.boardspaces;

import com.monopoly.entities.Entity;
import com.monopoly.entities.Player;

/**
 * Utility class
 */
public final class Utility extends Property{  

    /**
     * Constructor for Utility
     */
    public Utility(String name, int id, int price, Entity owner) {
        super(name, "Utility", id, price, owner); 
    } 

    /**
     * Gets the amount of utilities owned
     * @return amount of utilities owned
     */ 
    public int getRent(Player player) {
        int count = 0;
        for (Property p : getOwner().getProperties()) {
            if (p instanceof Utility) count++;
        }
        return player.getRoll() * (count == 1 ? 4 : 10);
    }

    public int getChanceRent(int roll) { 
        return roll*10;
    }
    
    /**
     * Charges rent to the player who lands on the property
     */
    @Override public void chargeRent(Player player) { 
        player.pay(getOwner(), getRent());
    } 
}