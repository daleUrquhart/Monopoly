/**
 * Tax Space
 */

package com.monopoly;

/**
 * Tax object
 */
final class Tax extends BoardSpace {

    /**
     * Amount charged by tax
     */
    private final int amount; 

    /**
     * Constructor for Tax
     */
    Tax(String name, int id, int amount) {
        super(name, id);
        this.amount = amount; 
    }

    /**
     * Gets the amount of tax due
     */
    int getTax() {
        return amount;
    }

    /**
     * Charges the tax
     * @param p player being charged
     * @return true for if the charge was succesful
     */
    void charge(Player p, Banker banker) { 
        p.debit(getTax()); 
        banker.credit(getTax());
    }
}
