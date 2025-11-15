/**
 * Go To Jail Space
 */

package com.monopoly;

/**
 * GoToJail class
 */
public final class GoToJail extends BoardSpace { 

    /**
     * Constructor for GoToJail
     */
    GoToJail(Jail jail) {
        super("Go To Jail", 30); 
    }
    
    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {
        game.sendToJail(current);
        mp.showMessage("Go directly to Jail. Do not pass Go, do not collect $200! ");
        if(game.getDice().doubles()) {game.increment(game.getTurnIndex());} //Do not go again from doubles if landed on go to jail, re-increment turn index
    }
}
