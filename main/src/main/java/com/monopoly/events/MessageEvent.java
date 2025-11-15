package com.monopoly.events;

import com.monopoly.GameController;

/**
 * Represents a text message that should be displayed to the player.
 */
public final class MessageEvent extends GameEvent {
    private final String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }

    @Override public void execute(GameController controller) {
        controller.getView().getMessagePane().showMessage(getMessage());
    }
}
