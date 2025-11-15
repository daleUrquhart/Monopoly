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
 * Handles actions from card drawn from community chest or chance
 */
final class CardManager extends BoardSpace {

    /**
     * Path to resources
     */
    private static final String PATH = "/com/monopoly/";

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
     * @param name Name of the deck (Commmunity Chest / Chance)
     * @param id Location of the deck's spot on the board
     * @param deck Deck to work with
     */
    CardManager(String name, int id, List<Card> deck) {
        super(name, id);
        this.deck = deck;
        rand = new Random();
        chance = deck.get(0).isChance();
    }

    @Override
    void onLand(Player current, Game game, MessagePane mp, GameController controller) {
        Card card = draw(game);
        mp.showMessage("Welcome to the "+current.getLocation().getName()+" square! Your card draw is:\n"+ card.toString());
        CardManager.handle(card, game, controller, mp); 
    }

    /**
     * Draws a card from deck, handles all events resulting from the card, retruns the card instance drawn
     */
    Card draw(Game game) {
        if(deck.isEmpty()) {
            try {deck = chance ? Card.getChanceDeck(getClass().getResourceAsStream(PATH + "cards.csv")) : 
                                 Card.getCCDeck(getClass().getResourceAsStream(PATH + "cards.csv"));}
            catch(IOException e) {System.out.println("Only bad programmers get errors, and Dale Urquhart wrote this code!");}
        }

        int index = rand.nextInt(deck.size()); 
        Card drawn = deck.get(index); deck.remove(index);
        return drawn;
    } 
    
    /**
     * Handles the actions described on drawn
     */
    static void handle(Card card, Game game, GameController controller, MessagePane mp) {
        int total;
        Player p = game.getCurrentPlayer();
        Utility utility;
        Railroad rr;
        Banker banker = Banker.getInstance();  

        // Card credits teh player (a possibly negetive) amount
        if(card.isCredit()) {
            if(card.getPayment() < 0) {
                if(p.canAfford(card.getPayment() * -1)) {
                    p.credit(card.getPayment());
                } else if(p.getNetWorth() > (card.getPayment() * -1)) {
                    mp.clearMessages();
                    mp.showMessage(card.getName()+"\nYou can not afford the "+card.getPayment()*-1+" payment, liquidate some assets.");
                    mp.showAck("Submit Payment", () -> handle(card, game, controller, mp));
                } else game.bankruptPlayer(p, banker, controller);
            }
            else p.credit(card.getPayment());
        }
        // Card bequeaths a get out of jail free card unto the player 
        if(card.isGetOutOfJail())   p.addJailCard();
        // Card sends player to jail
        if(card.isGoToJail())       game.sendToJail(p);
        // Card advances player advanceBy steps
        if(card.isAdvanceBy())      {
            game.movePlayerTo(p, p.getLocation().getId() + card.getSteps());
            if(p.getLocation() instanceof Property) controller.handleProperty() ;
            else controller.handleSpecialSquare();
        }
        // Card proceeds player to specific property
        if(card.isAdvanceTo())      {
            int starting = p.getLocation().getId();
            game.movePlayerTo(p, card.getLocation());
            if(starting > p.getLocation().getId()) game.getGo().reward(p);
        }
        // Card requires a payment to every player
        if(card.isPerPlayer())      {
            for(Player player : game.getPlayers()) {player.credit(card.getPlayerAmount());}
            int ammount = card.getPlayerAmount() * game.getPlayerCount();

            if(p.canAfford(ammount)) {
                for(Player payee : game.getPlayers()) {
                    if(p.equals(payee)) continue;
                    p.pay(payee, ammount);
                }
            } else if(p.getNetWorth() > ammount) {
                mp.clearMessages();
                mp.showMessage(card.getName()+"\nYou can not afford the "+ammount+" payment, liquidate some assets.");
                mp.showAck("Submit Payment", () -> handle(card, game, controller, mp));
            } else game.bankruptPlayer(p, banker, controller);  
        }
        // Card requires a payment to the bank for every development
        if(card.isPerDevelopment()) {
            total = p.getTotalHouses() * card.getHouseCost() + p.getTotalHotels() * card.getHotelCost();
            if(p.canAfford(total)) {
                p.pay(Banker.getInstance(), total);
            }
            else game.bankruptPlayer(p, banker, controller);
        }
        // Card proceeds player to nearest RailRoad or Utility
        if(card.isNearest())        {
            if(card.getNearestType().equals("RR"))           {
                // If owned charge chance rent, else give option to buy
                //Find the nearest railroad
                int space = p.getLocation().getId() + 5 - (p.getLocation().getId() % 5);
                space += space % 10 == 0 ? 5 : 0;
                space -= space > 40 ? 40 : 0;

                // Move to the new location
                int starting = p.getLocation().getId();
                game.movePlayerTo(p, space);
                if(starting > p.getLocation().getId()) game.getGo().reward(p);

                // Handle new location
                rr = (Railroad) p.getLocation();
                if(!rr.getOwner().equals(banker) && !rr.getOwner().equals(p)) {rr.chargeChanceRent(p); } 
                else if(rr.getOwner().equals(banker))                         {controller.handleUnownedProperty();}
                else{controller.handleOwnedProperty();}
                
            }

            else if(card.getNearestType().equals("Utility")) {
                // If owned charge chance rent, else give option to buy
                //Find the nearest utility
                utility = p.getLocation().getId() > 11 && p.getLocation().getId() < 28 ? (Utility) game.getSpace(28) : (Utility) game.getSpace(12);
                
                // Move to the new location
                int starting = p.getLocation().getId();
                game.movePlayerTo(p, utility.getId());
                if(starting > p.getLocation().getId()) game.getGo().reward(p);

                //Handle new locaiton
                if(!utility.getOwner().equals(banker) && !utility.getOwner().equals(p)) {utility.chargeChanceRent(p);} 
                else if(utility.getOwner().equals(banker))                              {controller.handleUnownedProperty();}
                else{controller.handleOwnedProperty();}
            }
        }
    }
}