/**
 * Go Space creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-21
 */

package com.monopoly.boardspaces;

import com.monopoly.GameModel;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Player;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.MessageEvent;
import com.monopoly.events.PaymentEvent;

/**
 * Go Space
 */
public final class Go extends BoardSpace {

    /**
     * Reward for reaching Go
     */
    private static final int REWARD = 200; 

    /**
     * Singleton go instance
     */
    private static final Go INSTANCE = new Go();

    /**
     * Go Constructor
     */
    private Go() {
        super("Go", 0); 
    }

    /**
     * Gets the singleton go instance
     * @return Go instance for the game
     */
    public static Go getInstance() {
        return INSTANCE;
    }

    /**
     * Handles events for landing on square
     */
    @Override public GameEvent onLand(Player current, GameModel game) {
        CompositeEvent e = new CompositeEvent();
        e.add(new MessageEvent("Congratulations, " + current.getName() + "! You made it to Go, here is $"+getReward()+"!"));
        e.add(new PaymentEvent(Banker.getInstance(), current, getReward()));
        return e;
    }

    /**
     * Gets teh reward for reaching Go
     */
    static int getReward() {
        return REWARD;
    } 
}