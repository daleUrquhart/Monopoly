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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final String PATH = "../resources/com/monopoly/";

    private static final double SCALE = 2.5;

    private static final int SQ = 33;

    private static final int MID = 24;

    private static Scene scene;

    private GridPane pane;

    private Game game; 

    private ArrayList<Profile> profiles;

    private TextField countField;

    private TextField nameField;

    private Label nameLabel;

    private Label countLabel;

    private HBox nameBox;

    private HBox countBox;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        System.out.println("START");

        profiles = new ArrayList<>();
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
    }
    
    /**
     * Handles taking the input to recieve the numebr of players to play in the game
     *
     * @return Number of players to play in the game
     */
    void buildPlayers() { 
        countLabel = new Label("Enter the number of players you will have in your game: ");
        nameLabel = new Label("Enter the name for payer 1: ");  
        Button countButton = new Button("Press to submit");
        Button nameButton = new Button("Press to submit");
        countButton.setOnAction(e -> getCount());
        nameButton.setOnAction(e -> getNames());
        
        nameField = new TextField();
        countField = new TextField(); 

        countBox = new HBox(10, countLabel, countField, countButton); 
        nameBox = new HBox(10, nameLabel, nameField, nameButton);
 
        pane.add(countBox, 11, 0); 
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
                count = Integer.parseInt(countField.getText());
                if (count > 1 && MAX_PLAYERS >= count) {
                    valid = true;
                } else {
                    countLabel.setText("Bad input, max player amount is 4 and you need at least 2, try again. ");
                    countField.clear();
                    return;
                }
            } catch (NumberFormatException e) {
                countLabel.setText("That is not a valid input.. Try again. "); 
                countField.clear();
                return;
            } catch (Exception e) {
                System.out.println("General exception in getCount()"); 
            }
            countField.clear();
        } 
        pane.getChildren().remove(countBox);
        game.setPlayerCount(count);
        //Get names
        pane.add(nameBox, 11, 0); 
    }

    void getNames() {
        String name;
        ArrayList<String> names = new ArrayList<>();
         
        name = nameField.getText();
        if(names.contains(name)) {
            nameLabel.setText("Someone already has that name. Try again: ");
            nameField.clear();
            return;
        }
        else { 
            nameLabel.setText("Enter the name for player "+(names.size()+2));
            names.add(name);
            game.getPlayers().add(new Player(name)); 
            nameField.clear();
        } 

        if(game.getPlayers().size() == game.getPlayerCount()) {
            pane.getChildren().remove(nameBox);
            for(Player p : game.getPlayers()) {
                int i = profiles.size(); 
                profiles.add(new Profile(p)); 
                pane.add(profiles.get(i).main, i, 11); 
            }
        }
    }

    public static void main(String[] args) { 
        launch();
        //pane.add(profiles.get(i).main, 11, 0, 1, GridPane.REMAINING);  //code for primary player display
    }
}