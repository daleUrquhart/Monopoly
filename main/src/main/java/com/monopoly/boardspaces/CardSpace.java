package com.monopoly.boardspaces;

import java.io.IOException; 
import java.util.List;
import java.util.Random;

import com.monopoly.Card;
import com.monopoly.Game;
import com.monopoly.Player;
import com.monopoly.events.CardEventManager;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.GameEvent;
import com.monopoly.events.MessageEvent;

/**
 * Represents either a Community Chest or Chance Space
 * Handles drawing a card and returning the appropriate GameEvent(s)
 */
public class CardSpace extends BoardSpace {

    private static final String RESOURCES = "/com/monopoly/";
    private List<Card> deck;
    private final Random rand = new Random();
    private final boolean chance;

    public CardSpace(String name, int id, List<Card> deck) {
        super(name, id);
        this.deck = deck;
        this.chance = deck.get(0).isChance();
    }

    @Override public GameEvent onLand(Player current, Game game) {
        Card card = drawDeck(); 
        CompositeEvent e = new CompositeEvent();
        e.add(new MessageEvent("Welcome to " + getName() + "! Your card draw is:\n" + card.getName()));
        e.add(CardEventManager.createCardEvent(card, current, game));
        return e;
    }

    private Card drawDeck() {
        if (deck.isEmpty()) {
            try {
                deck = chance ? Card.getChanceDeck(getClass().getResourceAsStream(RESOURCES + "cards.csv"))
                              : Card.getCCDeck(getClass().getResourceAsStream(RESOURCES + "cards.csv"));
            } catch (IOException e) {
                System.err.println("Error loading deck: " + e);
            }
        }
        int index = rand.nextInt(deck.size());
        return deck.remove(index);
    }
}
