package com.monopoly.events;

import com.monopoly.GameController;

public final class MoveByEvent extends GameEvent {
    private final int offset;

    MoveByEvent(int offset) {
        this.offset = offset;
    }

    @Override public void execute(GameController controller) {
        controller.getModel().movePlayerBy(offset);
    }
}
