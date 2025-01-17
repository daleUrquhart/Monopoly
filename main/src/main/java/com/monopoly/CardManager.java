/**
 * Represents either a Community Chest or Chance Space
 *
 * @author Dale Urquhart
 * @since 2024-10-21
 */


package com.monopoly;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * CardManager class
 */
final class CardManager extends BoardSpace {

    /**
     * Deck
     */
    private List<Card> deck;

    /**
     * Random connection
     */
    private final Random rand;

    /**
     * Whether or not this is a chance deck 
     */
    private final boolean chance;

    /**
     * Constructor for CardManager Instance
     */
    CardManager(String name, int id, List<Card> deck) {
        super(name, id);
        this.deck = deck;
        rand = new Random();
        chance = deck.get(0).isChance();
    }

    /**
     * Draws a card from deck, handles all events resulting from the card, retruns the card instance drawn
     */
    Card draw(Game game) {
        if(deck.isEmpty()) {
            try {deck = chance ? Card.getChanceDeck("cards.csv") : Card.getCCDeck("cards.csv");}
            catch(IOException e) {System.out.println("Only bad programmers get errors, and Dale Urquhart wrote this code!");}
        }

        int index = rand.nextInt(deck.size()); 
        Card drawn = deck.get(index); deck.remove(index);
        return drawn;
    } 
    
    /**
     * Handles the actions described on drawn
     */
    static void handle(Card card, Game game, GameController controller) {
        int total;
        Player p = game.getCurrentPlayer();
        Utility utility;
        Railroad rr;
        Banker banker = Banker.getInstance(); 
        if(card.isGetOutOfJail())   {p.addJailCard();}
        if(card.isGoToJail())       {game.getJail().addPlayer(p);}
        if(card.isAdvanceBy())      {
            p.setLocation(game.getSpace(p.getLocation().getId() + card.getSteps()));
            game.isProperty();
        }
        if(card.isAdvanceTo())      {
            int starting = p.getLocation().getId();
            p.setLocation(game.getSpace(card.getLocation())); 
            if(starting > p.getLocation().getId()) {
                game.getGo().reward(p);
            }
            game.isProperty();
        }
        if(card.isPerPlayer())      {
            for(Player player : game.getPlayers()) {player.credit(card.getPlayerAmount());}
            p.debit(card.getPlayerAmount() * game.getPlayerCount());
        }
        if(card.isPerDevelopment()) {
            total = p.getTotalHouses() * card.getHouseCost() + p.getTotalHotels() * card.getHotelCost();
            p.debit(total);
            banker.credit(total);
        }
        
        if(card.isNearest())        {
            if(card.getNearestType().equals("RR"))           {
                // If owned charge chance rent, else give option to buy
                //Find the nearest railroad
                int space = p.getLocation().getId() + 5 - (p.getLocation().getId() % 5);
                space += space % 10 == 0 ? 5 : 0;
                space -= space > 40 ? 40 : 0;
                //Handle new location
                p.setLocation(game.getSpace(space));
                rr = (Railroad) p.getLocation();
                if(!rr.getOwner().equals(banker) && !rr.getOwner().equals(p)) {rr.chargeChanceRent(p); } 
                else if(rr.getOwner().equals(banker))                         {controller.handleUnownedProperty();}
                else{controller.handleOwnedProperty();}
                
            }

            else if(card.getNearestType().equals("Utility")) {
                // If owned charge chance rent, else give option to buy
                //Find the nearest utility
                utility = p.getLocation().getId() > 11 && p.getLocation().getId() < 28 ? (Utility) game.getSpace(28) : (Utility) game.getSpace(12);
                p.setLocation(utility);
                //Handle new locaiton
                if(!utility.getOwner().equals(banker) && !utility.getOwner().equals(p)) {utility.chargeChanceRent(p);} 
                else if(utility.getOwner().equals(banker))                              {controller.handleUnownedProperty();}
                else{controller.handleOwnedProperty();}
            }
        }
    }
}