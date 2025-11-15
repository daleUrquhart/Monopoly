package com.monopoly;

import java.util.ArrayList;

/**
 * Jail space
 */
public class Jail extends BoardSpace{ 

    /**
     * Bail amount
     */
    private static final int BAIL = 50;

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
    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {
        if(current.inJail()) mp.showMessage("You are in jail.");
        else if (hasJailed()) mp.showMessage("Welcome to the visitation center. Say hello to your friends. ");
        else mp.showMessage("Welcome to the visitation center. Better stay on the right side of these bars...");
    }

    /**
     * gets the bail amount
     * @return the bail amount
     */
    int getBail() {
        return BAIL;
    }

    void addPlayer(Player p) {
        jailedPlayers.add(p);
    }

    /**
     * Removes a player from jailed players
     * Resets player's jailed turns and places their jail status to false
     * @param p player to remove from jailed players
     */
    void removePlayer(Player p) {
        jailedPlayers.remove(p);
    }

    /**
     * Checks if there are players in jail
     * @return true for if there is a player in jail
     */
    boolean hasJailed() {
        return !jailedPlayers.isEmpty();
    } 
}
