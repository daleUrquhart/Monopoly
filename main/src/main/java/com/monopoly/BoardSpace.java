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

public class BoardSpace { 
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
        int index = p.getID();
        ImageView img = p.getPiece(); 
        Pos alignment = (index==0 ? Pos.TOP_LEFT : (index==1 ? Pos.TOP_RIGHT : (index==2 ? Pos.BOTTOM_LEFT : Pos.BOTTOM_RIGHT))); 
        StackPane.setAlignment(img, alignment); 
        tileStack.getChildren().add(img);  
        occupants.add(p);  
    }

    /**
     * Removes an occupant
     * @param p Player occupant to be removed
     */
    void removeOccupant(Player p) {
        tileStack.getChildren().remove(p.getPiece());  
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