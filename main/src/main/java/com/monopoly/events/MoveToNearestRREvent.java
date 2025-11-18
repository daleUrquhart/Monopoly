package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.GameModel;
import com.monopoly.boardspaces.Railroad;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Player;

/**
 * Moves the player to the nearest Railroad and handles special Chance/CC rules:
 * - If unowned: allow purchase from Bank
 * - If owned: pay owner twice the standard rent
 */
public class MoveToNearestRREvent extends GameEvent { 

    @Override public void execute(GameController controller) {
        GameModel game = controller.getModel();
        Player player = game.getCurrentPlayer();
        int currentId = player.getLocation().getId();

        int nearestId = 5 * ((currentId / 5) + 1);
        if (nearestId % 10 == 0) nearestId += 5;
        if (nearestId > 40) nearestId -= 40;

        game.movePlayerTo(nearestId);
        Railroad rr = (Railroad) game.getSpace(nearestId);

        // This could instead be PaymentEvent(..., ((RailRoad) property).getChanceRent()) or smth but wtvs
        if (!(rr.getOwner().equals(player) || rr.getOwner().equals(Banker.getInstance()))) {
            int rent = rr.getRent() * 2; 
            MessageEvent messageEvent = new MessageEvent("$" + rent + " rent owed to " + rr.getOwner().getName() + " for " + rr.getName());
            PaymentEvent paymentEvent = new PaymentEvent(player, rr.getOwner(), rent);
            messageEvent.execute(controller);
            paymentEvent.execute(controller); 
        } else {
            player.getLocation().onLand(player, controller.getModel()).execute(controller);
        }
    }
}
