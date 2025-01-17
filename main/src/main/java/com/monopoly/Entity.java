/**
 * Rerpresents the banker, holds all banker related functions
 * @author Dale Urquhart
 * @since 2024-10-20
 */

package com.monopoly;

import java.util.ArrayList;

/**
 * Banker object
*/
class Entity { 

    /**
     * Name of the Banker
    */
    private final String name;

    /**
     * Balance
    */
    private int balance;

    /**
     * Net worth
    */
    private int netWorth;

    /**
     * Banker's properties
    */
    private final ArrayList<Property> properties; 

    /**
     * Player super constructor
    */
    Entity(String name, int balance) {
        this.name = name;
        this.balance = balance;
        netWorth = balance;
        properties = new ArrayList<>();
    } 

    /**
     * Gets name
    * @return name
    */
    String getName() {
        return name;
    }

    /**
     * Gets balance
    * @return balance
    */
    int getBalance() {
        return balance;
    }

    /**
     * Gets the total assets of the player
    * @return the total assets
    */
    int getNetWorth() {
        return netWorth;
    } 
    
    /**
     * Adjusts networth of the player
    * @param adjustment the adjustment value of the networth 
    */
    void adjustNetWorth(int adjustment) { 
        netWorth += adjustment; 
    }

    /**
     * Debits the balance
    * @param adjustment amount to add to balance 
    */
    void debit(int adjustment) { 
        balance -= adjustment;
        adjustNetWorth(adjustment * -1); 
    }

    /**
     * Credits the balance
    * @param adjustment to the balance 
    */
    void credit(int adjustment) { 
        balance += adjustment;
        adjustNetWorth(adjustment); 
    }

    /**
     * Whether or not an ammount is affordable
    * @param adjustment the amount to check
    * @return true for if it can be afforded
    */
    boolean canAfford(int adjustment) {
        return getBalance() >= adjustment;
    }

    /**
     * Gets properties owned
    * @return properties owned
    */
    ArrayList<Property> getProperties() {
        return properties;
    }

    /**
     * Checks if a property's set is fully owned by the player
    * @param check property to check
    * @return true for if the property's set is fully aquired by the player, else false
    */
    boolean ownsSetFor(Property check) {
        int count = 0;
        for(Property p : getProperties()) { 
            if(p.getType().equals(check.getType())) { 
                count++;
            }
        }
        return count==check.getSetSize();
    } 

    /**
     * Removes a property from the player's possesion
    * @param property the property to remove 
    */
    void removeProperty(Property property) { 
        properties.remove(property); 
    }

    /**
     * Adds a property to the player's array of properties
    * @param newProperty the property to be added
    */
    void addProperty(Property newProperty) {
        properties.add(newProperty);
        //newProperty.setOwner(this);
    }

    /**
     * Handles the purchase of a new proerty for the plaeyr bought by auction
    * @param newProperty the property to buy
    * @param bid the amount the player bid for the property  
    */
    void buy(Property p, int bid, Game game) { 
        //This is all only unmortgaging or paying intrest
        if(p.isMortgaged()) {
            //Can we afford to unmortgage it
            if(canAfford((int) (bid+p.getMortgageValue()*1.1))) {
                System.out.print(p.toString() + "\nIs mortgaged, would you like to unmortgage it now, or do so later? ");
                if (GameView.getBoolInput("Buy", p.getName()+" is mortgaged, would you like to unmortgage it now, or pay it later?", "UnMortgage price: "+(int) (p.getMortgageValue() * 1.1)+"Intreset only price: "+(int) (p.getMortgageValue() * 0.1)+".")) {
                    p.unMortgage();
                } else {
                    System.out.println("Property remains mortgaged, intrest only payment made. ");
                    debit((int) (p.getMortgageValue() * 0.1));
                }
            }
            //Pay only mandatory intrest
            else {
                System.out.println("Property remains mortgaged, cannot afford to unmortgaged, only payment made. ");
                debit((int) (p.getMortgageValue() * 0.1));
            }
        }

        //Bid transactioning
        debit(bid);
        p.getOwner().credit(bid);
        p.getOwner().removeProperty(p); //this cannot be the best way to do that...
        p.setOwner(this);
        addProperty(p);  
    } 
}