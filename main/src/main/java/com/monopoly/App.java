/**
 * GUI for monopoly application
 * @author Dale Urquhart 
 */
package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

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
     * Scene for the ajvafx application
     */
    private static Scene scene;

    /**
     * Pane for the javafx application
     */
    private GridPane pane;

    /**
     * Game instance handling all backend game logic
     */
    private Game game; 

    /**
     * ArrayList managing player instances and their GUI counterpart
     */
    private ArrayList<Profile> profiles;

    /**
     * Primiary field for handling rudementary instruction
     */
    private TextField primaryField; 

    /**
     * Primary label for handling rudementary instruction
     */
    private Label primaryLabel; 

    /**
     * Primary button for handling rudementary instruction
     */
    Button primaryButton;

     /**
     * HBox for storing primary label, field and submit
     */
    private HBox primaryBox; 

    /**
     * ArrayList storing game pieces
     */
    private ArrayList<Image> pieces;

    /**
     * Start method for the Monopoly GUI, ground zero
     */
    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        System.out.println("START");

        profiles = new ArrayList<>();
        pieces = new ArrayList<>();
        pane = new GridPane();  
        game = new Game();   
        System.out.println("Game created succefully");
        
        buildTiles();
        System.out.println("Tiles built succesfully");

        scene = new Scene(pane, 600, 600);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        buildPlayers();
        System.out.println("Profiles built succesfully");
    } 

    /**
     * 
     */
    void buildTiles(){
        //Iterative tile build    
        for(int col = 0; col < 11; col++) {
            for(int row = 0; row < 11; row++) {
                try {
                    if(col!=0 && col!=10 && row!=0 && row!=10) {continue;}

                    InputStream inputStream = new FileInputStream(PATH+col+"_"+row+".png");
                    Image image = new Image(inputStream);
                    ImageView imageView = new ImageView();  

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
                    
                    pane.add(imageView, col, row);
                }
                catch(FileNotFoundException e) {
                    System.out.println("There aint no "+col+", "+row+" file, idiot");
                }
                catch(Exception e) {
                    System.out.println("\n\nWTF?!");
                    throw(e);
                }
            }
        } 

        //Center tile
        try {
            InputStream inputStream = new FileInputStream(PATH+"center_tile.png");
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView();
            imageView.setImage(image); 
            imageView.setFitHeight(9*MID*SCALE);
            imageView.setFitWidth(9*MID*SCALE);

            pane.add(imageView, 1, 1, 9, 9);
        } catch (FileNotFoundException e) {
            System.out.println("Aint no thang");
        }

        //Game pieces
        try {
            for(int i = 0; i < 8; i++) {                                         
                FileInputStream in = new FileInputStream(PATH+i+"_piece.png");
                pieces.add(new Image(in));
            }
        } catch (FileNotFoundException e) {
            System.out.println("What are you? Four years old? How are you still out here loosing the game pieces to the board game");
        } catch (Exception e) {
            System.out.println("Bad code idiot");
            throw(e);
        }
    }
    
    /**
     * Handles getting player data and creating Profiles
     */
    void buildPlayers() { 
        primaryLabel = new Label("Enter the number of players you will have in your game: "); 
        primaryButton = new Button("Press to submit"); 
        primaryButton.setOnAction(e -> getCount());
        primaryField = new TextField(); 

        primaryBox = new HBox(10, primaryLabel, primaryField, primaryButton);  

        pane.add(primaryBox, 11, 0); 
    }

    /**
     * Gets player count
     */
    void getCount() {
        final int MAX_PLAYERS = 4;
        boolean valid = false;   
        int count = 0;

        while(!valid) {
            try {
                count = Integer.parseInt(primaryField.getText());
                if (count > 1 && MAX_PLAYERS >= count) {
                    valid = true;
                } else {
                    primaryLabel.setText("Bad input, max player amount is 4 and you need at least 2, try again. ");
                    primaryField.clear();
                    return;
                }
            } catch (NumberFormatException e) {
                primaryLabel.setText("That is not a valid input.. Try again. "); 
                primaryField.clear();
                return;
            } catch (Exception e) {
                System.out.println("General exception in getCount()"); 
            }
            primaryField.clear();
        }  
        game.setPlayerCount(count);

        //Get names
        primaryLabel.setText("Enter the name for payer 1: ");  
        primaryButton.setOnAction(e -> getNames()); 
    }

    /**
     * Gets player names
     */
    void getNames() {
        String name;
        ArrayList<String> names = new ArrayList<>(); 
        Profile profile;

        name = primaryField.getText();
        //Bad input
        if(names.contains(name)) {
            primaryLabel.setText("Someone already has that name. Try again: ");
            primaryField.clear(); 
        }
        //Good input, select a piece
        else { 
            primaryLabel.setText("Enter the name for the next player "); 
            primaryField.clear();
             
            profile = new Profile(name, game.getGo());

            names.add(name);
            game.addPlayer(profile);  
            profiles.add(profile);
 
            getPiece(profile);  
        }
    }

    /**
     * Gets player's piece
     */
    void getPiece(Profile p) { 
        HBox pieceBox1 = new HBox(), pieceBox2 = new HBox();
        VBox pieceBoxes = new VBox(pieceBox1, pieceBox2);

        pane.getChildren().remove(primaryBox);
        pane.add(primaryLabel, 11, 0); 

        primaryLabel.setText("Select a piece for "+p.getName()+":");   
 
        for(int i = 0; i < pieces.size(); i++) {  
            ImageView viewer = new ImageView();
            viewer.setImage(pieces.get(i)); 
            viewer.setFitHeight(MID*SCALE);
            viewer.setFitWidth(MID*SCALE);
            viewer.setOnMouseClicked((MouseEvent event) -> handleSelction(p, viewer, pieceBoxes) );
            if(i < 4) {pieceBox1.getChildren().add(viewer); }
            else      {pieceBox2.getChildren().add(viewer); }
        }
        pane.add(pieceBoxes, 12, 0, 4, 2); 
    }

    /**
     * Handles piece selection
     * @param p Profile that the piece selection is being assigned to
     * @param viewer The image viewer selected for the profile
     * @param pieceBoxes The conatiner storing the image options
     */
    void handleSelction(Profile p, ImageView viewer, VBox pieceBoxes) { 

        p.setPiece(viewer); 
        pieces.remove(viewer.getImage()); 

        primaryLabel.setText("Enter the name for the next player ");
        
        pane.getChildren().remove(pieceBoxes);
        pane.getChildren().remove(primaryLabel);
        primaryBox.getChildren().addFirst(primaryLabel);
        if(game.getPlayers().size() != game.getPlayerCount()) { pane.add(primaryBox, 11, 0); }
        else                                                  { 
            profiles.get(0).flipCurrent();
            for(Profile profile : profiles) {
                if(profile.getCurrent()) {
                    pane.add(profile.getMain(), 11, 0, 1, GridPane.REMAINING); 
                } else {
                    pane.add(profile.getMain(), profiles.indexOf(profile), 11);
                }
            }
        } 
    }

    /**
     * Main method launching GUI
     * @param args cmd line inputs
     */
    public static void main(String[] args) { 
        launch();
    }
}