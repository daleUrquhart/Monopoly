/**
 * Go To Jail Space
 */

package com.monopoly;

/**
 * GoToJail class
 */
final class GoToJail extends BoardSpace {

    /**
     * Jail instance
     */
    Jail jail;

    /**
     * Constructor for GoToJail
     */
    GoToJail(Jail jail) {
        super("Go To Jail", 30);
        this.jail = jail;
    }

    /**
     * Gets the jail instance
     * @return the jail instance
     */
    Jail getJail() {
        return jail;
    }

    /**
     * Sends player to jail
     * @param p player to be jailed
     * @return true for if the action was carried out succesfully
     */
    void jail(Player p) { 
        p.flipJailed(); 
        p.setLocation(getJail());
    }
}
