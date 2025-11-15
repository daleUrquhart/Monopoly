/**
 * Tax Space
 */

package com.monopoly;

/**
 * Tax object
 */
public final class Tax extends BoardSpace {

    /**
     * Amount charged by tax
     */
    private final int amount; 

    /**
     * Constructor for Tax
     */
    Tax(String name, int id, int amount) {
        super(name, id);
        this.amount = amount; 
    }

    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {
        //If player can afford the tax pay it
        if(current.canAfford(getTax())) {
            current.pay(Banker.getInstance(), getTax());
            mp.showMessage("Uh oh! You have been charged "+getName()+"! You were charged $" + getTax() + "!");
            controller.enableRoll();
        } 
        // Wait until player has liquidated asssets to pay for taxes
        else if(!current.canAfford(getTax()) && current.getNetWorth() >= getTax()) {
            mp.clearMessages();
            mp.showAck("Submit Payment", () -> onLand(current, game, mp, controller));
            mp.showMessage("You must liquidate some assets to pay for your taxes"); 
        }
        //Player bankrupted by bank, not able ot pay thier taxes 
        else {
            mp.showMessage("Breaking! " + current.getName() + " can not afford their taxes and goes bankrupt! It was a good run"); 
            game.bankruptPlayer(current, Banker.getInstance(), controller);
            controller.enableRoll();
        }
        
    }
 
    /**
     * Gets the amount of tax due
     */
    int getTax() {
        return amount;
    } 
}
