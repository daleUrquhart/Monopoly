/**
 * Free Parking Space
 */

package com.monopoly.boardspaces;

import com.monopoly.GameModel;
import com.monopoly.entities.Player;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.MessageEvent;
import com.monopoly.events.VoidEvent;

/**
 * FreeParking class
 */
public final class FreeParking extends BoardSpace {
    /**
     * Singleton fp instance
     */
    private static final FreeParking INSTANCE = new FreeParking();

    /**
     * Free parking constructor
     */
    private FreeParking () { super("Free Parking", 20); }

    /**
     * Gets the singleton FP instance
     * @return FP instance for the game
     */
    public static FreeParking getInstance() {
        return INSTANCE;
    }

    /**
     * Handles logic for landing on FP
     * @param current Player that landed on SQ
     * @param game Game instance
     * @return Message to be displayed
     */
    @Override public GameEvent onLand(Player current, GameModel game) {    
        CompositeEvent res = new CompositeEvent();
        res.add(new MessageEvent("Welcome to free parking. Take a breather. "));
        res.add(new VoidEvent());
        return res;
    }
}