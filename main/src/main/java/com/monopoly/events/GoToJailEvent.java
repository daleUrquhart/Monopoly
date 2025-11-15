package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.Player;

public final class GoToJailEvent extends GameEvent {

    private final Player player;

    public GoToJailEvent(Player player) {
        this.player = player;
    }

    @Override public void execute(GameController controller) {
        controller.getModel().sendToJail(player); 
    } 
}
