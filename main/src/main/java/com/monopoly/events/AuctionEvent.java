package com.monopoly.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monopoly.GameController;
import com.monopoly.boardspaces.Property;
import com.monopoly.entities.Player;

public class AuctionEvent extends GameEvent {
    protected final Property property;
    protected final List<Player> bidders;
    protected int currentIndex = 0;

    // Tracks each player's bid
    protected final Map<Player, Integer> bids = new HashMap<>();

    public AuctionEvent(Property property, List<Player> bidders) {
        this.property = property;
        this.bidders = bidders;
    }

    @Override
    public void execute(GameController controller) {
        if (currentIndex >= bidders.size()) {
            conclude(controller);
            return;
        }

        Player current = bidders.get(currentIndex);

        controller.getView().getMessagePane().getBoolInput(
            current.getName() + ", bid on " + property.getName() +
            "\nCurrent bid is $" + getCurrentHighestBid(),
            wantsToBid -> {
                if (wantsToBid) {
                    handleBid(controller, current);
                } else {
                    nextBidder(controller);
                }
            }
        );
    }

    protected void handleBid(GameController controller, Player current) {
        controller.getView().getMessagePane().getIntInput(
            "Enter Bid",
            "Bid higher than current $" + getCurrentHighestBid(),
            "Your balance: $" + current.getBalance(),
            getCurrentHighestBid(),
            current.getBalance(),
            bid -> {
                placeBid(current, bid);
                nextBidder(controller);
            }
        );
    }

    protected void nextBidder(GameController controller) {
        currentIndex++;
        execute(controller);
    }

    protected void conclude(GameController controller) {
        Player winner = getHighestBidder();

        if (winner != null) {
            controller.getView().getMessagePane().showMessage(
                winner.getName() + " wins " + property.getName() +
                " for $" + getCurrentHighestBid()
            );
            controller.buy(winner, property.getOwner(), property, getCurrentHighestBid());
        } else {
            controller.getView().getMessagePane().showMessage("No one bid. Property remains unsold.");
        }

        controller.enableRoll();
    }

    protected int getCurrentHighestBid() {
        return bids.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    protected void placeBid(Player player, int bid) {
        bids.put(player, bid);
    }

    protected Player getHighestBidder() {
        Player best = null;
        int max = 0;

        for (Map.Entry<Player, Integer> entry : bids.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                best = entry.getKey();
            }
        }

        return best;
    }
}
