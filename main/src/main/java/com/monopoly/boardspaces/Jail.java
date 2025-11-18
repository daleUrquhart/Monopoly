package com.monopoly.boardspaces;

import java.util.ArrayList;

import com.monopoly.GameModel;
import com.monopoly.entities.Player;
import com.monopoly.events.GameEvent;
import com.monopoly.events.MessageEvent;

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
     * Singleton jail instance
     */
    private static final Jail INSTANCE = new Jail();

    /**
     * Constructor for Just Visiting
    */
    private Jail() {
        super("Jail", 10);
        this.jailedPlayers = new ArrayList<>();
    }
    @Override public GameEvent onLand(Player current, GameModel game) {
        String message;
        if(current.inJail()) message = "You are in jail.";
        else if (hasJailed()) message = "Welcome to the visitation center. Say hello to your friends. ";
        else message = "Welcome to the visitation center. Better stay on the right side of these bars...";
        return new MessageEvent(message);
    }

    /**
     * Gets the singleton jail instance
     * @return Jail instance for the game
     */
    public static Jail getInstance() {
        return INSTANCE;
    }

    /**
     * gets the bail amount
     * @return the bail amount
     */
    public int getBail() {
        return BAIL;
    }

    public void addPlayer(Player p) {
        jailedPlayers.add(p);
    }

    /**
     * Removes a player from jailed players
     * Resets player's jailed turns and places their jail status to false
     * @param p player to remove from jailed players
     */
    public void removePlayer(Player p) {
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
