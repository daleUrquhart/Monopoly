/**
 * Free Parking Space
 */

package com.monopoly;

/**
 * FreeParking class
 */
final class FreeParking extends BoardSpace {
    FreeParking () {
        super("Free Parking", 20);
    }

    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {    
        mp.showMessage("Welcome to free parking. Take a breather. ");
    }
}