/**
 * Railroad location for the board
 */

package com.monopoly;

/**
 * Railroad class
 */
public final class Railroad extends Property{

    /**
     * Constructor for Railroad
     */
    Railroad(String name, int id, int price, Banker owner) {
        super(name, "Railroad", id, price, owner);
    }

    /**
     * Gets the rent charged at the proerty
     */
    @Override
    int getRent() {
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
    @Override
    void chargeRent(Player player) {
        int rent = getRent();
        player.debit(rent);
        getOwner().credit(rent);
    }

    /**
     * Charges chance rent to the plauer who lands on the property
     */
    void chargeChanceRent(Player player) {
        int rent = getRent() * 2;
        player.debit(rent);
        getOwner().credit(rent);
    }
}