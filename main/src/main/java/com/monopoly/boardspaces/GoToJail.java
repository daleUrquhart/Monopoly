/**
 * Go To Jail Space
 */

package com.monopoly.boardspaces;

import com.monopoly.GameModel;
import com.monopoly.entities.Player;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.GoToJailEvent;
import com.monopoly.events.MessageEvent;
import com.monopoly.events.VoidEvent;

/**
 * GoToJail class
 */
public final class GoToJail extends BoardSpace { 

    /**
     * Singleton go instance
     */
    private static final GoToJail INSTANCE = new GoToJail();

    /**
     * Constructor for GoToJail
     */
    private GoToJail() {
        super("Go To Jail", 30); 
    }

    /**
     * Gets the singleton GTJ instance
     * @return GTJ instance for the game
     */
    public static GoToJail getInstance() {
        return INSTANCE;
    }
    
    @Override public GameEvent onLand(Player current, GameModel game) {
        CompositeEvent e = new CompositeEvent();
        e.add(new GoToJailEvent(current));
        e.add(new MessageEvent("Go directly to Jail. Do not pass Go, do not collect $200"));
        e.add(new VoidEvent());
        return e;
    }
}
