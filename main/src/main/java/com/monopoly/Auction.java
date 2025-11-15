package com.monopoly;

import java.util.List;

import com.monopoly.boardspaces.Property;

/**
 * Auction class for handling auction flow
 */
public class Auction {

    /**
     * Property up for auction
     */
    private final Property property;

    /**
     * Bidders in the running to purhcase
     */
    private final List<Player> bidders;

    /**
     * Highest current bidder
     */
    private Player highestBidder;

    /**
     * Highest current bid
     */
    private int highestBid;

    /**
     * Constructor
     */
    public Auction(Property property, List<Player> bidders) {
        this.property = property;
        this.bidders = bidders;
        this.highestBid = 0;
    }

    /**
     * Places a new bid on the property, updating highest values
     */
    void placeBid(Player bidder, int amount) {
        if (amount > highestBid && amount <= bidder.getBalance()) {
            highestBid = amount;
            highestBidder = bidder;
        }
    }

    /**
     * Gets lsit fo bidders
     */
    List<Player> getBidders() { return bidders; }

    /**
     * Gets property for auction
     */
    Property getProperty() { return property; }

    /**
     * Gets highest bidder
     */
    Player getHighestBidder() { return highestBidder; }

    /**
     * Gets highest bid
     */
    int getHighestBid() { return highestBid; }
}
