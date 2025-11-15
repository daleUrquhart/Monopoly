package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.Player;

public final class GotJailCardEvent extends GameEvent {

    private final Player player;

    public GotJailCardEvent(Player player) { 
        this.player = player; 
    }

    @Override public void execute(GameController controller) {
        player.addJailCard();
    }
}
