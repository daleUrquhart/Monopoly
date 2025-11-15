package com.monopoly.events;

import java.util.ArrayList;
import java.util.List;

import com.monopoly.GameController;

public final class CompositeEvent extends GameEvent {
    private final List<GameEvent> events = new ArrayList<>(); 

    public CompositeEvent add(GameEvent event) {
        events.add(event);
        return this;
    }

    @Override public void execute(GameController controller) {
        for (GameEvent e : events) {
            e.execute(controller);
        }
    }
}
