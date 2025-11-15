package com.monopoly.events;

import com.monopoly.Entity;
import com.monopoly.Game;
import com.monopoly.GameController;
import com.monopoly.MessagePane;
import com.monopoly.Player;

public class PaymentEvent extends GameEvent {
    private final Entity recipient;
    private final int amount; 
    private final Entity sender;

    public PaymentEvent(Entity sender, Entity recipient, int amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    @Override public void execute(GameController controller) {
        tryPayment(controller);
    }

    private void tryPayment(GameController controller) {
        Game game = controller.getModel(); 
        MessagePane mp = controller.getView().getMessagePane();

        if(sender instanceof Player) {
            if (sender.canAfford(amount)) {
                sender.pay(recipient, amount);
                mp.showMessage("Paid $" + amount + " to " + recipient.getName());
            } 
            else if (sender.getNetWorth() >= amount) {
                // pause and wait for liquidation
                mp.clearMessages();
                mp.showAck(
                    "You cannot afford $" + amount + ". Liquidate assets and click OK to continue.",
                    () -> tryPayment(controller) // retry after user clicks
                );
            } 
            else {
                // bankrupt
                game.bankruptPlayer((Player) sender, recipient, controller); 
                controller.handleWinner();
            }
        }
        
    }
}
