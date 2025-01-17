/**
 * Represents a Card for either Community Chest or Chance
 */

package com.monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 

/**
 * Represents a card in the game that can have various actions like payments, advancing to locations, and special rules.
 */
class Card {

    /**
     * The name or description of the card, often used to indicate the card's action.
     */
    private final String name; 

    /**
     * Indicates whether the card is a "Get Out of Jail Free" card.
     */
    private final boolean getOutOfJail;

    /**
     * Indicates whether the card sends the player to jail.
     */
    private final boolean goToJail;

    /**
     * Indicates if the card's action depends on the number of houses or hotels owned by the player.
     */
    private final boolean perDevelopment;

    /**
     * The amount to pay per house owned, if the card requires payment per development.
     */
    private final int houseCost;

    /**
     * The amount to pay per hotel owned, if the card requires payment per development.
     */
    private final int hotelCost;

    /**
     * Indicates whether the card requires the player to advance to a specific location on the board.
     */
    private final boolean advanceTo;

    /**
     * The location to which the player must advance if the card has an "advance to" action.
     */
    private final int location;

    /**
     * Indicates whether the card advances the player by a specific number of steps.
     */
    private final boolean advanceBy;

    /**
     * The number of steps the player must advance, if the card requires advancing by a specific number of spaces.
     */
    private final int steps;

    /**
     * Indicates whether the card's action involves other players, typically requiring payment or collection based on the number of players.
     */
    private final boolean perPlayer;

    /**
     * The amount to be paid or collected from each player, if the card involves interaction with other players.
     */
    private final int playerAmount;

    /**
     * Indicates whether the card is part of the Chance deck.
     */
    private final boolean chance;

    /**
     * Whetehr or not the player is advancing to the nearest property type specified
     */
    private final boolean nearest;

    /**
     * Nearest property type specified
     */
    private final String nearestType; 

    /**
     * Constructor to initialize a Card object with all fields.
     *
     * @param name The name or description of the card.
     * @param payment The monetary effect (positive for collect, negative for pay).
     * @param getOutOfJail Whether this card is a Get Out of Jail Free card.
     * @param goToJail Whether this card sends the player to Jail.
     * @param perDevelopment Whether the card applies per house or hotel owned.
     * @param houseCost Cost per house if applicable.
     * @param hotelCost Cost per hotel if applicable.
     * @param advanceTo Whether the card requires advancing to a location.
     * @param location The specific location to advance to, if applicable.
     * @param advanceBy Whether the card advances by a number of steps.
     * @param steps The number of steps to advance, if applicable.
     * @param perPlayer Whether the card's action depends on the number of players.
     * @param playerAmount The amount paid or collected per player.
     * @param chance Whether the card is a Chance card.
     * @param communityChest Whether the card is a Community Chest card.
     * @param rentMultiplier Multiplier applied to rent payments if applicable.
     * @param rollAndValueMultiplier Multiplier based on dice roll outcomes.
     * @param nearest Whether or not the player is advancing to the nearest specified proeprty type
     * @param nearestType The nearest property type specified
     */
    Card(String name, int payment, boolean getOutOfJail, boolean goToJail, boolean perDevelopment, int houseCost, int hotelCost,
                boolean advanceTo, int location, boolean advanceBy, int steps, boolean perPlayer, int playerAmount,
                boolean chance, boolean communityChest, boolean nearest, String nearestType) {
        this.name = name; 
        this.getOutOfJail = getOutOfJail;
        this.goToJail = goToJail;
        this.perDevelopment = perDevelopment;
        this.houseCost = houseCost;
        this.hotelCost = hotelCost;
        this.advanceTo = advanceTo;
        this.location = location;
        this.advanceBy = advanceBy;
        this.steps = steps;
        this.perPlayer = perPlayer;
        this.playerAmount = playerAmount;
        this.chance = chance; 
        this.nearest = nearest;
        this.nearestType = nearestType;
    }

    /**
     * Gets the name or description of the card.
     *
     * @return the name of the card
     */
    String getName() {
        return name;
    }
 
    /**
     * Checks if the card is a "Get Out of Jail Free" card.
     *
     * @return true if it's a "Get Out of Jail Free" card, false otherwise
     */
    boolean isGetOutOfJail() {
        return getOutOfJail;
    }

    /**
     * Checks if the card sends the player to jail.
     *
     * @return true if the card sends the player to jail, false otherwise
     */
    boolean isGoToJail() {
        return goToJail;
    }

    /**
     * Checks if the card's action is related to property developments (houses or hotels).
     *
     * @return true if the card is per development, false otherwise
     */
    boolean isPerDevelopment() {
        return perDevelopment;
    }

    /**
     * Gets the cost per house for cards involving property development payments.
     *
     * @return the cost per house, or 0 if not applicable
     */
    int getHouseCost() {
        return houseCost;
    }

    /**
     * Gets the cost per hotel for cards involving property development payments.
     *
     * @return the cost per hotel, or 0 if not applicable
     */
    int getHotelCost() {
        return hotelCost;
    }

    /**
     * Checks if the card requires the player to advance to a specific location.
     *
     * @return true if the card involves advancing to a location, false otherwise
     */
    boolean isAdvanceTo() {
        return advanceTo;
    }

    /**
     * Gets the specific location to which the player must advance.
     *
     * @return the location object, or null if not applicable
     */
    int getLocation() {
        return location;
    }

    /**
     * Checks if the card advances the player by a specific number of steps.
     *
     * @return true if the card involves advancing by steps, false otherwise
     */
    boolean isAdvanceBy() {
        return advanceBy;
    }

    /**
     * Gets the number of steps the player must advance.
     *
     * @return the number of steps, or 0 if not applicable
     */
    int getSteps() {
        return steps;
    }

    /**
     * Checks if the card's action involves other players.
     *
     * @return true if the card involves per-player interaction, false otherwise
     */
    boolean isPerPlayer() {
        return perPlayer;
    }

    /**
     * Gets the amount to be paid or collected from each player.
     *
     * @return the amount per player, or 0 if not applicable
     */
    int getPlayerAmount() {
        return playerAmount;
    }

    /**
     * Checks if the card belongs to the Chance deck.
     *
     * @return true if the card is a Chance card, false otherwise
     */
    boolean isChance() {
        return chance;
    } 

    /**
     * Gets whether or not the player is advancing to the nearest specified property type 
     * @return Whetehr or not the player is advancing to the nearest specified property type 
     */
    boolean isNearest() {
        return nearest;
    }

    /**
     * Gets the nearest specified proerty type
     * @return nearest specified property type 
     */
    String getNearestType() {
        return nearestType;
    }

    /**
     * Loads all card data from a CSV file and returns a list of Card objects.
     *
     * @param filePath The path to the CSV file containing the card data.
     * @return A list of Card objects loaded from the CSV file.
     * @throws IOException If there is an error reading the CSV file.
     */
    static ArrayList<Card> getCCDeck(String filePath) throws IOException { 
        ArrayList<Card> ccDeck = new ArrayList<>();  

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            // Read each line in the CSV and create a Card object
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Check if the line has exactly 17 columns
                if (values.length < 17) {
                    System.err.println("Skipping invalid line (not enough columns): " + line);
                    continue; // Skip to the next line
                } 
                try {
                    Card card = new Card(
                            values[0],
                            Integer.parseInt(values[1]),
                            Boolean.parseBoolean(values[2]),
                            Boolean.parseBoolean(values[3]),
                            Boolean.parseBoolean(values[4]),
                            Integer.parseInt(values[5].isEmpty() ? "0" : values[5]),
                            Integer.parseInt(values[6].isEmpty() ? "0" : values[6]),
                            Boolean.parseBoolean(values[7]),
                            Integer.parseInt(values[8].isEmpty() ? "0" : values[8]), 
                            Boolean.parseBoolean(values[9]),
                            Integer.parseInt(values[10].isEmpty() ? "0" : values[10]),
                            Boolean.parseBoolean(values[11]),
                            Integer.parseInt(values[12].isEmpty() ? "0" : values[12]),
                            Boolean.parseBoolean(values[13]),
                            Boolean.parseBoolean(values[14]), 
                            Boolean.parseBoolean(values[15]),
                            values[16].isEmpty() ? "null" : values[16]
                    ); 
                    if(!card.isChance()) {ccDeck.add(card);}
                    
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Array index error with line: " + line + " - " + e.getMessage());
                }
            }
        }
        return ccDeck;
    }

    /**
     * Loads all card data from a CSV file and returns a list of Card objects.
     *
     * @param filePath The path to the CSV file containing the card data.
     * @return A list of Card objects loaded from the CSV file.
     * @throws IOException If there is an error reading the CSV file.
     */
    static ArrayList<Card> getChanceDeck(String filePath) throws IOException {
        ArrayList<Card> chanceDeck = new ArrayList<>();  

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            // Read each line in the CSV and create a Card object
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Check if the line has exactly 17 columns
                if (values.length < 17) {
                    System.err.println("Skipping invalid line (not enough columns): " + line);
                    continue; // Skip to the next line
                } 
                try {
                    Card card = new Card(
                            values[0],
                            Integer.parseInt(values[1]),
                            Boolean.parseBoolean(values[2]),
                            Boolean.parseBoolean(values[3]),
                            Boolean.parseBoolean(values[4]),
                            Integer.parseInt(values[5].isEmpty() ? "0" : values[5]),
                            Integer.parseInt(values[6].isEmpty() ? "0" : values[6]),
                            Boolean.parseBoolean(values[7]),
                            Integer.parseInt(values[8].isEmpty() ? "0" : values[8]), 
                            Boolean.parseBoolean(values[9]),
                            Integer.parseInt(values[10].isEmpty() ? "0" : values[10]),
                            Boolean.parseBoolean(values[11]),
                            Integer.parseInt(values[12].isEmpty() ? "0" : values[12]),
                            Boolean.parseBoolean(values[13]),
                            Boolean.parseBoolean(values[14]), 
                            Boolean.parseBoolean(values[15]),
                            values[16].isEmpty() ? "null" : values[16]
                    );

                    if(card.isChance()) {chanceDeck.add(card);}  
                    
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Array index error with line: " + line + " - " + e.getMessage());
                }
            }
        }   

        return chanceDeck;
    }

    /**
     * Returns a string representation of the card in a format suitable for initializing it.
     *
     * @return a string representation of the card
     
    String toRepr() {
        return "new Card(\"" + getName() + "\", " + getPayment() + ", " + isGetOutOfJail() + ", "
                + isGoToJail() + ", " + isPerDevelopment() + ", " + getHouseCost() + ", "
                + getHotelCost() + ", " + isAdvanceTo() + ", \"" + getLocation() + "\", "
                + isAdvanceBy() + ", " + getSteps() + ", " + isPerPlayer() + ", "
                + getPlayerAmount() + ", " + isChance() + ", " + isCommunityChest() + ", "
                + isNearest()+", "+getNearestType()+ ");";  
    }
     */
     
    /**
     * Returns a string representation of the card
     */
    @Override
    public String toString() {
        return getName();
    }
}
