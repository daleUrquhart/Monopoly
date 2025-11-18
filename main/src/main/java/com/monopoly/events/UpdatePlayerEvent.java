package com.monopoly.events;

import com.monopoly.GameController;
import com.monopoly.entities.Player;

public class UpdatePlayerEvent extends GameEvent{ 
    private final Player p;
    public UpdatePlayerEvent(Player p){this.p=p;}
    @Override public void execute(GameController controller) {controller.getView().getMessagePane().displayCurrent(p, controller);}
}
