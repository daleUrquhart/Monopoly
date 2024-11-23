package com.monopoly;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Player profile display, showing stats of a player in the game
 */
final class Profile {
    
    /**
     * Hard coded scale of player image icons
     */
    private final static int SQ =  25; 

    /**
     * The profile's GUI component
     */
    private final VBox main; 

    /**
     * Represents a player's game piece
     */
    private ImageView piece;

    /**
     * Player instance the profile is representing
     */
    private Player player; 

    /**
     * Order player was added
     */
    private int index;

    /**
     * Primary constructor for Profile instances
     */ 
    Profile(ImageView piece, int index) {    
        setPiece(piece);
        this.index = index;
        this.main = new VBox(10, this.piece); 
    }

    /**
     * Sets the proile's player instance
     * @param player Profile's player instance
     */
    void setPlayer(Player player) {
        this.player= player;
        this.main.getChildren().addLast(new Label(player.toString()));
    }

    /**
     * Gets the main component
     * @return Main GUI component for representing the Profile instance
     */
    VBox getMain() {
        return main;
    } 

    /**
     * Gets the idnex order of which the profile was created
     * @return Order the pkayer was added
     */
    int getIndex() {
        return index;
    }
    
    /**
     * Gets the player's playing piece
     * @return The player's playing piece
     */
    ImageView getPiece() {
        return piece;
    }
    
    /**
     * GEts the player instance the profile is representing
     * @return Player instance the profile is representing
     */
    Player getPlayer() {
        return  player;
    }

    /**
     * Sets the player's game piece
     * @param piece the player's game piece
     */
    void setPiece(ImageView piece) {
        this.piece = piece;
        this.piece.setFitHeight(SQ);
        this.piece.setFitWidth(SQ); 
    }
}
