/**
 * Utility location for the board
 */

package com.monopoly;

/**
 * Utility class
 */
final class Utility extends Property{  

    /**
     * Constructor for Utility
     */
    Utility(String name, int id, int price, Entity owner) {
        super(name, "Utility", id, price, owner); 
    } 

    /**
     * Gets the amount of utilities owned
     * @return amount of utilities owned
     */ 
    int getRent(Player player) {
        int count = 0;
        for (Property p : getOwner().getProperties()) {
            if (p instanceof Utility) {
                count += 1;
            }
        }
        return player.getRoll() * (count == 1 ? 4 : 10);
    }

    /**
     * Charges rent to the player who lands on the property
     */
    @Override
    void chargeRent(Player player) { 
        int rent = getRent(player);
        player.debit(rent);
        getOwner().credit(rent);
    }

    /**
     * Charges chance rent to the player who lands on the property (10x roll no matter what)
     */ 
    void chargeChanceRent(Player player) {
        int rent = (player.getRoll() * 10);
        player.debit(rent);
        getOwner().credit(rent);
    }
}