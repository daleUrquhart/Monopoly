package com.monopoly.events;

import com.monopoly.Banker;
import com.monopoly.Game;
import com.monopoly.GameController;
import com.monopoly.Player;
import com.monopoly.boardspaces.Railroad;

/**
 * Moves the player to the nearest Railroad and handles special Chance/CC rules:
 * - If unowned: allow purchase from Bank
 * - If owned: pay owner twice the standard rent
 */
public class MoveToNearestRREvent extends GameEvent { 

    @Override public void execute(GameController controller) {
        Game game = controller.getModel();
        Player player = game.getCurrentPlayer();
        int currentId = player.getLocation().getId();

        int nearestId = 5 * ((currentId / 5) + 1);
        if (nearestId % 10 == 0) nearestId += 5;
        if (nearestId > 40) nearestId -= 40;

        game.movePlayerTo(nearestId);
        Railroad rr = (Railroad) game.getSpace(nearestId);
        Banker banker = Banker.getInstance();

        if (rr.getOwner().equals(banker)) {
            controller.handleUnownedProperty();
        } else if (!rr.getOwner().equals(player)) {
            int rent = rr.getRent() * 2; 
            MessageEvent messageEvent = new MessageEvent("$" + rent + " rent owed to " + rr.getOwner().getName() + " for " + rr.getName());
            PaymentEvent paymentEvent = new PaymentEvent(player, rr.getOwner(), rent);
            messageEvent.execute(controller);
            paymentEvent.execute(controller); 
        } else {
            controller.handleOwnedProperty();
        }
    }
}
