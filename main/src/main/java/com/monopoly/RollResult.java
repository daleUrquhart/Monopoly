package com.monopoly;

import com.monopoly.boardspaces.BoardSpace;

public class RollResult {
    private final boolean go;
    private final BoardSpace space;
    private final int roll;
    private final int doublesOutcome;

    public RollResult(boolean go, BoardSpace space, int roll, int doublesOutcome) {
        this.go = go;
        this.space = space;
        this.roll = roll;
        this.doublesOutcome = doublesOutcome;
    }

    public BoardSpace getSpace() {return space;}
    public boolean passedGo() {return go;}
    public int getRoll() {return roll;}
    public int getDoublesOutcome() {return doublesOutcome;} 
}
