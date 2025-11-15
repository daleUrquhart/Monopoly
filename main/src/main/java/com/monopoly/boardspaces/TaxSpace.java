/**
 * Tax Space
 */

package com.monopoly.boardspaces;

import com.monopoly.Banker;
import com.monopoly.Game;
import com.monopoly.Player;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.MessageEvent;
import com.monopoly.events.PaymentEvent;

/**
 * Tax object
 */
public final class TaxSpace extends BoardSpace {

    /**
     * Amount charged by tax
     */
    private final int amount; 

    /**
     * Constructor for Tax
     */
    public TaxSpace(String name, int id, int amount) {
        super(name, id);
        this.amount = amount; 
    }

    @Override public GameEvent onLand(Player current, Game game) { 
        CompositeEvent e = new CompositeEvent();
        e.add(new MessageEvent("Uh oh! You have been charged "+getName()+"! You were charged $" + getTax() + "!"));
        e.add(new PaymentEvent(Banker.getInstance(), current, amount)); 
        return e;
    }
 
    /**
     * Gets the amount of tax due
     */
    int getTax() {
        return amount;
    } 
}
