package com.monopoly.events;

import com.monopoly.GameController;

public final class MovementEvent extends GameEvent {
    private final int targetIndex;

    public MovementEvent(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override public void execute(GameController controller) {
        controller.getModel().movePlayerTo(targetIndex);
    }
}
