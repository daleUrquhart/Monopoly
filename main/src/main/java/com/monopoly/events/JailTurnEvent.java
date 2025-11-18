package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.GameModel;
import com.monopoly.boardspaces.Jail;
import com.monopoly.entities.Player;

public final class JailTurnEvent extends GameEvent {

    @Override
    public void execute(GameController controller) {
        GameModel game = controller.getModel();
        Player player = game.getCurrentPlayer();
        Jail jail = Jail.getInstance();

        // 1. Max turns? (Auto-logic only. UI handled in controller.)
        if (player.getJailedTurns() >= 3) {
            new MaxJailTurnsEvent().execute(controller);
            return;
        }

        // 2. Build options for controller (UI)
        boolean canPay = player.canAfford(jail.getBail()) || player.getNetWorth()>jail.getBail();
        boolean hasCard = player.ownsJailCard();

        controller.promptJailOptions(canPay, hasCard);
    }
}
