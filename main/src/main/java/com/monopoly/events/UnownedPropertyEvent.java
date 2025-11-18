package com.monopoly.events;

import com.monopoly.GameController; 

public class UnownedPropertyEvent extends GameEvent { 

    @Override public void execute(GameController controller) {
        controller.handleUnownedProperty();
    }
}
