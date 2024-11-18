/**
 * BoardSpace creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import java.util.ArrayList;

class BoardSpace {

    /**
     * Name of the space
     */
    private String name;

    /**
     * ID of the space
     */
    private int id;

    /**
     * Players on the space
     */
    private ArrayList<Player> occupants;

    /**
     * All-args constructor
     * @param name name of the sapce
     * @param id id of the space
     */
    BoardSpace(String name, int id) {
        this.name = name;
        this.id = id;
        this.occupants = new ArrayList<Player>();
    }

    /**
     * Getter for the name of the space
     * @return the name of the space
     */
    String getName() {
        return name;
    }

    /**
     * Getter for the id of the space
     * @return the id of the space
     */
    int getId() {
        return id;
    }

    /**
     * Gets the list of players on the space
     * @return the occupants of the property
     */
    ArrayList<Player> getOccupants() {
        return occupants;
    }

    /**
     * Adds an occupant to the space
     * @param p player arrived
     */
    void addOccupant(Player p) {
        occupants.add(p);
    }

    /**
     * Removes an occupant
     * @param occupant to be removed
     */
    void removeOccupant(Player p) {
        occupants.remove(p);
    } 

    /**
     * String rep of BoardSpace
     */
    @Override
    public String toString() {
        return "Name: "+getName()+"\nSpace: "+(getId()+1);
    }
}