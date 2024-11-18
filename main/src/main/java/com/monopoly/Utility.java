/**
 * Utility location for the board
 */

package com.monopoly;

import java.util.ArrayList;

/**
 * Utility class
 */
final class Utility extends Property{ 

    /**
     * Players in the game 
     */
    private final ArrayList<Player> players;

    /**
     * Constructor for Utility
     */
    protected Utility(String name, int id, int price, Banker owner, Banker banker, ArrayList<Player> players) {
        super(name, "Utility", id, price, owner, banker);
        this.players = players;
    }

    /**
     * Getst eh current player
     */
    protected Player getCurrentPlayer() {
        for(Player p : players) {
            if(p.getCurrent()) {
                return p;
            } 
        }
        return null; 
    }

    /**
     * Gets the amount of utilities owned
     * @return amount of utilities owned
     */
    @Override
    protected int getRent() {
        int count = 0;
        for (Property p : getOwner().getProperties()) {
            if (p instanceof Utility) {
                count += 1;
            }
        }
        return getCurrentPlayer().getRoll() * (count == 1 ? 4 : 10);
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
     * Charges chance rent to the player who lands on the property (10x roll no matter what)
     */ 
    protected void chargeChanceRent(Player player) {
        int rent = (player.getRoll() * 10);
        player.debit(rent);
        getOwner().credit(rent);
    }
}