/**
 * Railroad location for the board
 */

package com.monopoly.boardspaces;

import com.monopoly.entities.Banker;
import com.monopoly.entities.Player;

/**
 * Railroad class
 */
public final class Railroad extends Property{

    /**
     * Constructor for Railroad
     */
    public Railroad(String name, int id, int price, Banker owner) {
        super(name, "Railroad", id, price, owner);
    }

    /**
     * Gets the rent charged at the proerty
     */
    @Override public int getRent() {
        double rent = 12.5;
        for (Property p : getOwner().getProperties()) {
            if (p instanceof Railroad) {
                rent *= 2;
            }
        } 
        return (int) rent;
    }

    /**
     * Charges rent to the player who lands on the property
     */
    @Override public void chargeRent(Player player) {
        player.pay(getOwner(), getRent());
    } 
}