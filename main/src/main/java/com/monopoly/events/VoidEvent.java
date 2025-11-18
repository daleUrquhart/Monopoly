package com.monopoly.events;

import com.monopoly.GameController;

public class VoidEvent extends GameEvent{
    @Override public void execute(GameController controller) {controller.enableRoll();};
}
