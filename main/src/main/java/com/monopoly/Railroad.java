/**
 * Railroad location for the board
 */

package com.monopoly;

/**
 * Railroad class
 */
final class Railroad extends Property{

    /**
     * Constructor for Railroad
     */
    protected Railroad(String name, int id, int price, Banker owner, Banker banker) {
        super(name, "Railroad", id, price, owner, banker);
    }

    /**
     * Gets the rent charged at the proerty
     */
    @Override
    protected int getRent() {
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
    protected void chargeRent(Player player) {
        player.debit(getRent());
        getOwner().credit(getRent());
    }

    /**
     * Charges chance rent to the plauer who lands on the property
     */
    protected void chargeChanceRent(Player player) {
        player.debit(getRent() * 2);
        getOwner().credit(getRent() * 2);
    }
}