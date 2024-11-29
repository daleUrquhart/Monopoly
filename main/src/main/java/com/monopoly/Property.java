/**
 * Property creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

/**
 * Property object class
 */
class Property extends BoardSpace{

    /**
     * Default rent
     */
    private int defaultRent;

    /**
     * Amount of properties in the set
     */
    private int setSize;

    /**
     * Type of the property
     */
    private final String type;

    /**
     * Number of houses on the property
     */
    private int houses;

    /**
     * Price of the property
     */
    private final int price;

    /**
     * Whether or not the property has a hotel
     */
    private boolean hotel;

    /**
     * Whether or not the property is mortgaged
     */
    private boolean mortgaged;

    /**
     * Rent prices for houses
     */
    private int[] houseRent;

    /**
     * Rent price for a hotel
     */
    private int hotelRent;

    /**
     * Mortgage value of the property
     */
    private int mortgageValue;

    /**
     * Development cost of the property
     */
    private int developmentCost;

    /**
     * The owner of the property
     */
    private Entity owner; 

    /**
     * Default constructor for a properrty object
     */
    Property(Entity owner, String name, String type, int setSize, int id, int rent, int h1, int h2, int h3,int h4, int hotel, int mortgage, int developmentCost, int price) {
        super(name, id);
        this.price = price;
        setOwner(owner);
        this.type = type;
        this.setSize = setSize;
        defaultRent = rent;
        houseRent = new int[]{h1, h2, h3, h4};
        hotelRent = hotel;
        mortgageValue = mortgage;
        this.developmentCost = developmentCost; 
    }

    /**
     * Utility and Railroad constructor
     */
    Property(String name, String type, int id, int price, Entity owner) {
        super(name, id);
        setOwner(owner);
        this.type = type;
        this.price = price; 
    } 

    /**
     * Gets the size of the set of properties of the same type
     * @return the size of the set of properties of the same type
     */
    int getSetSize() {
        return setSize;
    }

    /**
     * Gets the mortgage value for the property
     * @return Mortgage value of the property
     */
    int getMortgageValue() {
        return mortgageValue;
    }

    /**
     * Assign a new owner to the property
     * Also adds the property to the new owner's list of properties
     * @param newOwner the new owner of the property
     */
    final void setOwner(Entity newOwner) {
        if(getOwner() != null) {getOwner().adjustNetWorth((int) (getPrice() / -2));}
        
        owner = newOwner;
        getOwner().addProperty(this);
        getOwner().adjustNetWorth((int) (getPrice() / 2));
    }
    
    /**
     * Mortgages the property. A mortgaged property does not count to net worth
     * @return true for if the action was succesful
     */
    void mortgage() { 
        getOwner().adjustNetWorth((int) (getPrice() / 2) * -1);
        getOwner().credit(getMortgageValue());
        Banker.getInstance().debit(getMortgageValue());
        mortgaged = true;
    }

    /**
     * Unmortgages a property
     */
    void unMortgage() {
        getOwner().adjustNetWorth((int) (getPrice() / 2));
        getOwner().debit((int) (getMortgageValue() * 1.1));
        Banker.getInstance().credit((int) (getMortgageValue() * 1.1));
        mortgaged = false;
    } 

    /**
     * Gets the type of the property set
     * @return the type of the property set
     */
    String getType() {
        return type;
    }

    /**
     * Gets the owner of the property
     * @return the owner of the property
     */
    Entity getOwner() {
        return owner;
    }

    /**
     * Getter for the default rent amount
     * @return the default rent amount
     */
    private int getDefaultRent() {
        return defaultRent;
    }

    /**
     * Whether or not the property has houses or hotel (is developed)
     * @return true for if there is developments on the property
     */
    boolean developed() {
        return getHouses()!=0 || hasHotel();
    }

    /**
     * Checks if the property is mortaged or not
     * @return true if the property is mortgaged
     */
    boolean isMortgaged() {
        return mortgaged;
    }

    /**
     * Gets the status of whetehr or not a hotel is on the property
     * @return true for if a hotel exists
     */
    boolean hasHotel() {
        return hotel;
    }

    /**
     * Gets the number of hosues on the proerty
     * @return the number of houses on the property
     */
    int getHouses() {
        return houses;
    }

    /**
     * Buys a development on the property
     */
    void buyDevelopment() {
        getOwner().adjustNetWorth((int) (getDevelopmentCost() / 2)); 
        getOwner().debit(getDevelopmentCost());
        Banker.getInstance().credit(getDevelopmentCost());

        if(getHouses() == 4) {
            hotel = true;
            houses = 0;
        } else {
            houses++;
        }
    }

    /**
     * Sells a development on the property
     */
    void sellDevelopment() {
        Banker.getInstance().debit((int) (getDevelopmentCost() / 2));
        getOwner().credit((int) (getDevelopmentCost() / 2));
        getOwner().adjustNetWorth((int) (getDevelopmentCost() / -2));

        if(hasHotel()) {
            houses = 4;
            hotel = false;
        } else {
            houses--;
        }
    }

    /**
     * Gets the price of the property to buy
     * @return the price of the property
     */
    int getPrice() {
        return price;
    }

    /**
     * Charges rent to the player who lands on the property
     * No rent charged when banker owns the property
     */
    void chargeRent(Player renter) {
        renter.debit(getRent());
        getOwner().credit(getRent());
    }

    /**
     * Getter for the current rent value of the property
     * @return the rent value of the proerty
     */
    int getRent() {
        int total = getDefaultRent();

        if (getOwner().ownsSetFor(this) && !developed()) {
            total *= 2;
        } else if (developed()) {
            total = hasHotel() ? getHotelRent() : getHouseRent();
        }

        return total;
    }

    /**
     * Gets rent for specified number of hosues
     */
    int getHouseRent() {
        return houseRent[getHouses()-1];
    } 

    /**
     * Gets rent for a hotel
     */
    int getHotelRent() {
        return hotelRent;
    }

    /**
     * Gets the development cost of a property
     * @return development cost of a proerty
     */
    int getDevelopmentCost() {
        return developmentCost;
    } 

    @Override
    public String toString() {
        return super.toString()+"\nPrice: $"+getPrice();
    }
}