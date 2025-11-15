/**
 * Go Space creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-21
 */

package com.monopoly;

/**
 * Go Space
 */
public final class Go extends BoardSpace {

    /**
     * Reward for reaching Go
     */
    private static final int REWARD = 200; 

    /**
     * Go Constructor
     */
    Go(String name, int id) {
        super(name, id); 
    }

    /**
     * Handles events for landing on square
     */
    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {
        reward(current);
        mp.showMessage("Congratulations, " + current.getName() + "! You made it to Go! ");
    }

    /**
     * Gets teh reward for reaching Go
     */
    static int getReward() {
        return REWARD;
    }

    /**
     * Disburse the reward to a player
     * @param p player to reward
     */
    void reward(Player p) {
        p.credit(getReward());
        Banker.getInstance().debit(getReward());
    }
}