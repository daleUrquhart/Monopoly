package com.monopoly.events;

import com.monopoly.Card;
import com.monopoly.GameModel;
import com.monopoly.boardspaces.Go;
import com.monopoly.entities.Banker;
import com.monopoly.entities.Player;

/**
 * Factory for converting a Card into a GameEvent
 */
public final class CardEventManager { 

    public static GameEvent createCardEvent(Card card, Player player, GameModel game) {
        CompositeEvent result = new CompositeEvent();
        Banker banker = Banker.getInstance();

        // Credit or debit
        if (card.isCredit()) {
            if(card.getPayment() < 0) result.add(new PaymentEvent(banker, player, card.getPayment()));
            else result.add(new PaymentEvent(player, banker, -card.getPayment()));
        }

        // Jail effects
        if (card.isGetOutOfJail()) result.add(new GotJailCardEvent(player));
        if (card.isGoToJail()) result.add(new GoToJailEvent(player));

        // Move events
        if (card.isAdvanceBy()) result.add(new MoveByEvent(card.getSteps()));
        if (card.isAdvanceTo()) {
            result.add(new MovementEvent(card.getLocation()));
            if(game.passedGo(player.getLocation().getId()+card.getLocation())) result.add(Go.getInstance().onLand(player, game));
        }

        // Per-player payments
        if (card.isPerPlayer()) result.add(new PaymentPerPlayerEvent(card.getPlayerAmount()));

        // Payment per development
        if (card.isPerDevelopment()) result.add(new PaymentPerDevelopmentEvent(card.getHouseCost(), card.getHotelCost()));

        // Nearest property movement
        if (card.isNearest()) {
            switch (card.getNearestType()) {
                case "RR": 
                    result.add(new MoveToNearestRREvent());
                    break;
                case "Utility": result.add(new MoveToNearestUtilityEvent());
            }
        }
 
        return result;
    }
}
