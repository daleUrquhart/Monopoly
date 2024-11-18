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
final class Go extends BoardSpace {

    /**
     * Reward for reaching Go
     */
    private static final int REWARD = 200;

    /**
     * Banker 
     */
    Banker banker;

    /**
     * Go Constructor
     * @param banker the banker
     */
    Go(Banker banker) {
        super("Go", 0);
        this.banker = banker;
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
        banker.debit(getReward());
    }
}