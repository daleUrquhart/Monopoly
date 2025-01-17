/**
 * Delegates board building actions
 */
package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * BoardBuilder manages construction of the board
 */
class BoardBuilder {
    
    /**
     * Path to resources directory
     */
    private static final String PATH = "../resources/com/monopoly/";

    /**
     * Scale for the board sizing
     */
    private static final double SCALE = 2.5;

    /**
     * Larger length for board tile components
     */
    private static final int SQ = 33;

    /**
     * Smaller length for board tile components
     */
    private static final int MID = 24;
    

    /**
     * Builds the map destinations iteravely using indexed filenames
     */ 
     void buildTiles(Game game, GridPane pane) {
        int count = -1; 
        int col = 10, row = 10;  

        try {
            for(int i = 0; i < 40; i++) {   
                InputStream inputStream = new FileInputStream(PATH+col+"_"+row+".png");
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView();   

                count++;
                imageView.setImage(image); 

                if(col == 0 || col == 10) {
                    if(row == 0 || row == 10) {
                        imageView.setFitHeight(SCALE * SQ);
                        imageView.setFitWidth(SCALE * SQ);
                    }
                    else {
                        imageView.setFitHeight(SCALE * MID);
                        imageView.setFitWidth(SCALE * SQ);
                    }                        
                }
                else {
                    imageView.setFitHeight(SCALE * SQ);
                    imageView.setFitWidth(SCALE * MID);
                } 
                
                game.getMap()[count].setTile(imageView);
                pane.add(game.getSpace(count).getStack(), col, row);  

                if (col == 10 && row < 10) row++;
                else if (col == 0 && row > 0) row--;
                else if (row == 10 && col > 0) col--;
                else if (row == 0 && col < 10) col++;
            } 
        } catch (FileNotFoundException e) {
            System.out.println("Aint no thang");
        } catch (Exception e) {
            System.out.println("Bad code idiot");
            throw(e);
        }
    }

    /**
     * Builds the dice 
     */
    GridPane buildDice(Game game, GridPane pane) {
        GridPane dicePane = new GridPane();
        try {
            InputStream d1Stream = new FileInputStream(PATH + "6_die.png");
            Image d1 = new Image(d1Stream);
            ImageView d1View = new ImageView(d1);
            d1View.setFitHeight(MID * SCALE);
            d1View.setFitWidth(MID * SCALE);

            InputStream d2Stream = new FileInputStream(PATH + "6_die.png");
            Image d2 = new Image(d2Stream);
            ImageView d2View = new ImageView(d2);
            d2View.setFitHeight(MID * SCALE);
            d2View.setFitWidth(MID * SCALE);

            // GridPane for dice, and adding dicePane to game
            dicePane = new GridPane();
            dicePane.add(d1View, 0, 0);
            dicePane.add(d2View, 1, 0);
            dicePane.setHgap(10);
            dicePane.setAlignment(Pos.CENTER); 
            game.setDice(new Dice(dicePane)); 
        } catch(FileNotFoundException e) {
            System.err.println("Dice piece not found in BoardBuilder. Full message:\n"+e);
        }
        return dicePane;
    }

    /**
     * Builds the center of the board
     */
    StackPane buildCenter(Game game, GridPane pane) {
        StackPane center = new StackPane();
        try {
            // Center tile 
            InputStream boardStream = new FileInputStream(PATH + "center_tile.png");
            Image board = new Image(boardStream);
            ImageView boardView = new ImageView(board);
            boardView.setFitHeight(9 * MID * SCALE);
            boardView.setFitWidth(9 * MID * SCALE);
 
            // StackPane for board and dice
            center.getChildren().addAll(boardView);
            center.setAlignment(Pos.CENTER);
            
            // Add to main grid
            pane.add(center, 1, 1, 9, 9);
        } catch(FileNotFoundException e) {
            System.err.println("Game piece not found in BoardBuilder. Full message:\n"+e);
        } 
        return center;
    }
}
