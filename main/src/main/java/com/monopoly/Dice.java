/**
 * Dice creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import java.util.ArrayList;
import java.util.Random;

/**
 * Dice object class
 */
class Dice {

    /**
     * Random connection
     */
    private Random rand;

    /**
     * Stores roll 1 value
     */
    private int r1;

    /**
     * Stores roll 2 value
     */
    private int r2;

    /**
     * Current player
     */
    ArrayList<Player> players;

    /**
     * Default constructor for Dice
     */
    Dice(ArrayList<Player> players) {
        rand = new Random();
        this.players = players;
    }

    /**
     * Roll simulator for dice
     * @return random int. between 2, and 12
     */
    int roll() {
        setR1(rand.nextInt(6)+1);
        setR2(rand.nextInt(6)+1);
        getCurrent().setRoll(getR1() + getR2());
        return getR1()+getR2();
    }

    /**
     * Gets teh current player
     * @return the current player
     */
    Player getCurrent() {
        for(Player p : getPlayers()) {
            if(p.getCurrent()) {
                return p;
            }
        }
        return null;
    }

    /**
     * Gets the players array list
     * @return the players array lsit
     */
    ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * get roll from dice 1
     */
    int getR1() {
        return r1;
    }

    /**
     * get roll from dice 2
     */
    int getR2() {
        return r2;
    }

    /**
     * Set roll from dice 1
     */
    void setR1(int rolled) {
        r1 = rolled;
    }

    /**
     * Set roll from dice 2
     */
    void setR2(int rolled) {
        r2 = rolled;
    }

    /**
     * Returns whether or not the roll was doubles
     * @return true for if the roll was doubles
     */
    boolean doubles() {
        return getR1()==getR2();
    }
}