/**
 * Driver file for Monopoly program
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

/**
 * Main driver class
 */
class Main {

    /**
     * Driver method
     *
     * @param args
     */
    public static void main(String[] args) {
        Game game = new Game(); 
        game.gameLoop();
    }
}