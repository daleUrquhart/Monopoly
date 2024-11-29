package com.monopoly;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Player profile display, showing stats of a player in the game
 */
final class Profile {
    
    /**
     * Hard coded scale of player image icons
     */
    private final static int SQ =  25; 

    /**
     * Icon square size
     */
    private final static int ICON_SCALE = 20;
    /**
     * The profile's secondary GUI component
     */
    private final GridPane secondary; 

    /**
     * The profile's primary GUI component
     */
    private final GridPane primary;

    /**
     * Represents a player's game piece
     */
    private ImageView piece;

    /**
     * Represents piece as a smaller icon on the tiles
     */
    private  ImageView icon;

    /**
     * Player instance the profile is representing
     */
    private Player player; 

    /**
     * Order player was added
     */
    private final int index; 

    /**
     * Primary constructor for Profile instances
     */ 
    Profile(ImageView piece, int index) {    
        setPiece(piece);
        this.index = index;
        this.secondary = new GridPane();  
        this.primary = new GridPane(); 
    } 

    /**
     * Finalizes construction with player being made
     */
    void build(Player player, Game game) {
        this.player = player;
        setPrimary(game);
        setSecondary();
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
     * Gets the player's icon piece
     * @return The player's icon piece
     */
    ImageView getIcon() {
        return icon;
    }

    /**
     * GEts the player instance the profile is representing
     * @return Player instance the profile is representing
     */
    Player getPlayer() {
        return  player;
    }

    /**
     * Getst eh primary label
     */
    GridPane getPrimary(Game game) { 
        return primary;
    } 

    /**
     * Returns the seconadry view of the profile data
     */
    GridPane getSecondary() {
        return secondary;
    }
    
    /**
     * Builds the primary label
     */
    void setPrimary(Game game) {
        Label prompt = new Label("It is your turn, click the dice to roll and move and end your turn, "+
                                   "or from the following options first:");  
        HBox pBox;
        Button buyDevelopmentBoxB, mortgageB, unMortgageB, auctionB, privateSaleB, sellB, sellDevelopmentB; 
        primary.add(getPiece(), 0, 0);
        primary.add(prompt, 0, 1);
        primary.add(new Label(getPlayer().toString()), 0, 2);
        
        // Lengthy button display logic (dont offer to mortage an already mortgaged property, etc.)
        for(Property p : getPlayer().getProperties()) { 
            buyDevelopmentBoxB = new Button("Buy Development");
            buyDevelopmentBoxB.setOnMouseClicked(e -> p.buyDevelopment());

            mortgageB = new Button("Mortgage property");
            mortgageB.setOnMouseClicked(e -> p.mortgage());

            unMortgageB = new Button("Un Mortgage");
            unMortgageB.setOnMouseClicked(e -> p.unMortgage());

            auctionB = new Button("Auction");
            auctionB.setOnMouseClicked(e -> game.handleAuction(p));

            privateSaleB = new Button("Private Sale");
            privateSaleB.setOnMouseClicked(e -> game.handlePrivateSale(p));

            sellB = new Button("Sell to Bank");
            sellB.setOnMouseClicked(e -> getPlayer().sell(p));

            sellDevelopmentB = new Button("Sell development");
            sellDevelopmentB.setOnMouseClicked(e -> p.sellDevelopment());

            pBox = new HBox();
            pBox.getChildren().add(new Label(p.toString()));

            if(!p.hasHotel() && getPlayer().canAfford(p.getDevelopmentCost())) {
                pBox.getChildren().add(buyDevelopmentBoxB);
            }
            if(!p.developed()) {
                if(!p.isMortgaged()) {
                    pBox.getChildren().add(mortgageB);
                }
                pBox.getChildren().addAll(auctionB, privateSaleB); 
            } else {

            }
            if(p.isMortgaged() && getPlayer().canAfford((int) (p.getMortgageValue() * 1.1))) {
                pBox.getChildren().add(unMortgageB);
            }
            //primary.getChildren().add(pBox);
        } 
    }

    /**
     * Builds the secondary display of the player
     */
    void setSecondary() {
        this.secondary.add(getPiece(), 0,0);
        this.secondary.add(new Label(player.toString()), 0, 1);
    } 

    /**
     * Sets the player's game piece
     * @param piece the player's game piece
     */
    void setPiece(ImageView piece) {
        this.piece = piece;
        this.piece.setFitHeight(SQ);
        this.piece.setFitWidth(SQ); 

        setIcon();
    }

    /**
     * Sets the player's game icon
     * @param piece the player's game icon
     */
    void setIcon() {
        this.icon = new ImageView(piece.getImage());
        this.icon.setFitHeight(ICON_SCALE);
        this.icon.setFitWidth(ICON_SCALE); 
    } 
}
