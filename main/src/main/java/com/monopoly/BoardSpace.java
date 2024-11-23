/**
 * BoardSpace creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

class BoardSpace {

    /**
     * Offset coeffecient so pieces dont overlap eachotehr
     */
    private static final int SCALE = 20;
    /**
     * ImageView of the BoardSpace
     */
    private final StackPane tileStack;

    /**
     * Name of the space
     */
    private final String name;

    /**
     * ID of the space
     */
    private final int id;

    /**
     * Players on the space
     */
    private final ArrayList<Player> occupants;

    /**
     * All-args constructor
     * @param name name of the sapce
     * @param id id of the space
     */
    BoardSpace(String name, int id) {
        this.tileStack = new StackPane();
        this.name = name;
        this.id = id;
        this.occupants = new ArrayList<>();
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

    StackPane getStack() {
        return tileStack;
    }

    /**
     * Assigns the board space an image
     * @param location_img image of the tile
     */
    void setTile(ImageView img) {
        tileStack.getChildren().add(img); 
    }
    
    /**
     * Adds an occupant to the space
     * @param p player arrived
     */
    void addOccupant(Player p) { 
        occupants.add(p);  
        int i = p.getProfile().getIndex();
        Pos alignment = (i==1 ? Pos.TOP_LEFT : (i==2 ? Pos.TOP_RIGHT : (i==3 ? Pos.BOTTOM_LEFT : Pos.BOTTOM_RIGHT)));
        ImageView cp = new ImageView(p.getProfile().getPiece().getImage());
        StackPane.setAlignment(cp, alignment);
        cp.setFitHeight(SCALE);
        cp.setFitWidth(SCALE);
        tileStack.getChildren().add(cp);  
    }

    /**
     * Removes an occupant
     * @param occupant to be removed
     */
    void removeOccupant(Player p) {
        occupants.remove(p); 
        //tile.getChildren().remove(p.getProfile().get);    TODO
    } 

    /**
     * String rep of BoardSpace
     */
    @Override
    public String toString() {
        return "Name: "+getName()+"\nSpace: "+(getId()+1);
    }
}