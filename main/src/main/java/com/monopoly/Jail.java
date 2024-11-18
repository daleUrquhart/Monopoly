package com.monopoly;

import java.util.ArrayList;

/**
 * Jail space
 */
public class Jail extends BoardSpace{ 

    /**
     * Games's players 
     */
    ArrayList<Player> players;

    /**
     * Constructor for Just Visiting
    */
    Jail(ArrayList<Player> players) {
        super("Jail", 10);
        this.players = players;
    }

    /**
     * Checks if there are players in jail
     * @return true for if there is a player in jail
     */
    boolean hasJailed() {
        for(Player p : players) {
            if(p.inJail()) {
                return true;
            }
        } return false;
    } 
}
