package com.monopoly.events;

import com.monopoly.Banker;
import com.monopoly.Game;
import com.monopoly.GameController;
import com.monopoly.Player;
import com.monopoly.boardspaces.Utility;

/**
 * Moves the player to the nearest Utility and handles special Chance/CC rules:
 * - If unowned: allow purchase from Bank
 * - If owned: pay owner 10x the dice roll that landed player here
 */
public final class MoveToNearestUtilityEvent extends GameEvent {
    
    @Override public void execute(GameController controller) {
        Game game = controller.getModel();
        Player player = game.getCurrentPlayer();
        
        // Nearest utility IDs are 12 (Electric Company) and 28 (Water Works)
        int targetId = (player.getLocation().getId() < 28) ? 28 : 12;
        game.movePlayerTo(targetId);

        Utility utility = (Utility) game.getSpace(targetId);
        Banker banker = Banker.getInstance();

        if (utility.getOwner().equals(banker)) {
            controller.handleUnownedProperty(); 
        } else if (!utility.getOwner().equals(player)) {
            int multiplier = 10; 
            int rent = game.getDice().getRoll() * multiplier;
            MessageEvent messageEvent = new MessageEvent("$" + rent + " rent owed to " + utility.getOwner().getName() + " for " + utility.getName());
            PaymentEvent paymentEvent = new PaymentEvent(player, utility.getOwner(), rent);
            messageEvent.execute(controller);
            paymentEvent.execute(controller);
        } else {
            controller.handleOwnedProperty();
        }
    }
}
