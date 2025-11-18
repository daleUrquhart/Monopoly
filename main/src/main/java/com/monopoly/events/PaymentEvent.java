package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.MessagePane;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Entity;
import com.monopoly.entities.Player;

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
        // Maybe wrap this in a getBoolInput (if allowed, maybe have a forced payment event for ones that msut be paid)
        // Maybe only call a payment event once networth is checked andd bankrupt event otherwise
        // Anywho, not an issue for today
        MessagePane mp = controller.getView().getMessagePane();

        if(sender instanceof Player) {
            if (sender.canAfford(amount)) {
                sender.pay(recipient, amount);
                mp.showMessage("Paid $" + amount + " to " + recipient.getName());
                controller.enableRoll();     
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
                controller.handleBankruptcy(recipient, (Player)  sender);
            }
        }
        else Banker.getInstance().pay(recipient, amount);
    }
}
