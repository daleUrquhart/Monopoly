package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.Player;

public final class PaymentPerPlayerEvent extends GameEvent {
    private final int amount;

    public PaymentPerPlayerEvent(int amount) {
        this.amount = amount;
    }

    @Override public void execute(GameController controller) {
        Player current = controller.getModel().getCurrentPlayer();
        for (Player p : controller.getModel().getPlayers()) {
            if (p == current) continue;
            p.pay(current, amount);
        }
    }
}
