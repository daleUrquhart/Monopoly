package com.monopoly;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Player profile display, showing stats of a player in the game
 */
class Profile extends Player{
    
    /**
     * Hard coded scale of player image icons
     */
    private final static int SQ = 20; 

    /**
     * The profile's GUI component
     */
    private final VBox main; 

    /**
     * Represents a player's game piece
     */
    private ImageView piece;

    /**
     * Primary constructor for Profile instances
     */ 
    Profile(String name, BoardSpace location) {
        super(name, location);   
        piece = new ImageView();  
        VBox box = new VBox(10, piece, new Label(toString()));
        this.main = box;
    }

    /**
     * Gets the main component
     * @return Main GUI component for representing the Profile instance
     */
    VBox getMain() {
        return main;
    } 

    /**
     * Sets the player's game piece
     * @param piece the player's game piece
     */
    void setPiece(ImageView piece) {
        piece.setFitHeight(SQ);
        piece.setFitWidth(SQ);
        this.piece = piece;
        this.main.getChildren().addFirst(piece);
    }
}
