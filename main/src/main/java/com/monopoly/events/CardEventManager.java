package com.monopoly.events;

import com.monopoly.Banker;
import com.monopoly.Card;
import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Factory for converting a Card into a GameEvent
 */
public final class CardEventManager { 

    public static GameEvent createCardEvent(Card card, Player player, Game game) {
        CompositeEvent result = new CompositeEvent();
        Banker banker = Banker.getInstance();

        // Credit or debit
        if (card.isCredit()) {
            result.add(new MessageEvent("Card: " + card.getName() + " -> $" + card.getPayment()));
            result.add(new PaymentEvent(player, banker, card.getPayment()));
        }

        // Jail effects
        if (card.isGetOutOfJail()) result.add(new GotJailCardEvent(player));
        if (card.isGoToJail()) result.add(new GoToJailEvent(player));

        // Move events
        if (card.isAdvanceBy()) result.add(new MoveByEvent(card.getSteps()));
        if (card.isAdvanceTo()) result.add(new MovementEvent(card.getLocation()));

        // Per-player payments
        if (card.isPerPlayer()) result.add(new PaymentPerPlayerEvent(card.getPlayerAmount()));

        // Payment per development
        if (card.isPerDevelopment()) result.add(new PaymentPerDevelopmentEvent(card.getHouseCost(), card.getHotelCost()));

        // Nearest property movement
        if (card.isNearest()) {
            switch (card.getNearestType()) {
                case "RR": result.add(new MoveToNearestRREvent());
                case "Utility": result.add(new MoveToNearestUtilityEvent());
            }
        }
 
        return result;
    }
}
