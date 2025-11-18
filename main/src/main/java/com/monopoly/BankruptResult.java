package com.monopoly;

import java.util.List;

import com.monopoly.boardspaces.Property;
import com.monopoly.entities.Entity;
import com.monopoly.entities.Player;
import com.monopoly.events.CompositeEvent;

public class BankruptResult {

    private final Player bankrupted;
    private final Entity bankrupter;

    CompositeEvent transferred;
    private final List<Property> mortgagedNeedingDecision;
    private final CompositeEvent auctionList;

    private final boolean allAuctioned;
    private final int playersRemaining;

    public BankruptResult(
            Player bankrupted,
            Entity bankrupter,
            CompositeEvent transferred,
            List<Property> mortgagedNeedingDecision,
            CompositeEvent auctionList,
            boolean allAuctioned,
            int playersRemaining) {

        this.bankrupted = bankrupted;
        this.bankrupter = bankrupter;
        this.transferred = transferred;
        this.mortgagedNeedingDecision = mortgagedNeedingDecision;
        this.auctionList = auctionList;
        this.allAuctioned = allAuctioned;
        this.playersRemaining = playersRemaining;
    }

    public Player getBankrupted() { return bankrupted; }
    public Entity getBankrupter() { return bankrupter; }

    public CompositeEvent getTransferred() { return transferred; }
    public List<Property> getMortgagedNeedingDecision() { return mortgagedNeedingDecision; }
    public CompositeEvent getAuctionList() { return auctionList; }

    public boolean isAllAuctioned() { return allAuctioned; }
    public int getPlayersRemaining() { return playersRemaining; }
}
