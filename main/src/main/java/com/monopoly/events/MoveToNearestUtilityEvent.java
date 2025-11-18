package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.GameModel;
import com.monopoly.boardspaces.Utility;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Player;

/**
 * Moves the player to the nearest Utility and handles special Chance/CC rules:
 * - If unowned: allow purchase from Bank
 * - If owned: pay owner 10x the dice roll that landed player here
 */
public final class MoveToNearestUtilityEvent extends GameEvent {
    
    @Override public void execute(GameController controller) {
        GameModel game = controller.getModel();
        Player player = game.getCurrentPlayer();
        
        // Nearest utility IDs are 12 (Electric Company) and 28 (Water Works)
        int targetId = (player.getLocation().getId() < 28) ? 28 : 12;
        game.movePlayerTo(targetId);

        Utility utility = (Utility) game.getSpace(targetId);
        
        if (!(utility.getOwner().equals(player) && utility.getOwner().equals(Banker.getInstance()))) {
            int multiplier = 10; 
            int rent = game.getDice().getRoll() * multiplier;
            MessageEvent messageEvent = new MessageEvent("\n$" + rent + " rent owed to " + utility.getOwner().getName() + " for " + utility.getName());
            PaymentEvent paymentEvent = new PaymentEvent(player, utility.getOwner(), rent);
            messageEvent.execute(controller);
            paymentEvent.execute(controller);
        } else {
            player.getLocation().onLand(player, controller.getModel()).execute(controller);
        }
    }
}
