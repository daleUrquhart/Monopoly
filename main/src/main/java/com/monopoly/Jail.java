package com.monopoly;

import java.util.ArrayList;

/**
 * Jail space
 */
public class Jail extends BoardSpace{ 

    /**
     * Games's players 
     */
    ArrayList<Player> jailedPlayers;

    /**
     * Constructor for Just Visiting
    */
    Jail(String name, int id) {
        super(name, id);
        this.jailedPlayers = new ArrayList<>();
    }

    /**
     * Adds a player to the jailedPlayers
     * @param p player to ad to jailedPlayers
     */
    void addPlayer(Player p) {
        jailedPlayers.add(p);
        p.flipJailed();
        p.resetDoubleCount();
        p.setLocation(this); 
    }

    /**
     * Removes a player form jailed players
     * @param p player to remove from jailed players
     */
    void removePlayer(Player p) {
        jailedPlayers.remove(p);
        p.flipJailed(); 
    }

    /**
     * Checks if there are players in jail
     * @return true for if there is a player in jail
     */
    boolean hasJailed() {
        return jailedPlayers.isEmpty();
    } 
}
