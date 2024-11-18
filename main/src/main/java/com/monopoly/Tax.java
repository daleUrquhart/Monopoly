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
     * The banker 
     */
    Banker banker;

    /**
     * Constructor for Tax
     */
    protected Tax(String name, int id, int amount, Banker banker) {
        super(name, id);
        this.amount = amount;
        this.banker = banker;
    }

    /**
     * Gets the amount of tax due
     */
    protected int getTax() {
        return amount;
    }

    /**
     * Charges the tax
     * @param p player being charged
     * @return true for if the charge was succesful
     */
    protected void charge(Player p) { 
        p.debit(getTax()); 
        banker.credit(getTax());
    }
}
