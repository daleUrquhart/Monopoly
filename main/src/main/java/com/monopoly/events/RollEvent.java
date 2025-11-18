package com.monopoly.events;

import com.monopoly.GameController;

public class RollEvent extends GameEvent {
    
    @Override public void execute(GameController controller) {
        controller.showRollResults();

    }
}
