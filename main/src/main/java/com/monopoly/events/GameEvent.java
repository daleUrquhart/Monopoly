package com.monopoly.events;

import com.monopoly.GameController;

/**
 * Represents something that happened in the game.
 */
public abstract class GameEvent { 

    public abstract void execute(GameController controller); 
}
