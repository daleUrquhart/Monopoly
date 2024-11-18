package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        System.out.println("START");
        pane = new GridPane(); 
        game = new Game(); 
        
        //Iterative tile build    
        for(int col = 0; col < 11; col++) {
            for(int row = 0; row < 11; row++) {
                try {
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
        InputStream inputStream = new FileInputStream(PATH+"center_tile.png");
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView();
        imageView.setImage(image); 
        imageView.setFitHeight(9*MID*SCALE);
        imageView.setFitWidth(9*MID*SCALE);
        pane.add(imageView, 1, 1, 9, 9);
        
        System.out.println("Tiles built succesfully");

        profiles = new ArrayList<>();
        for(Player p : game.getPlayers()) {
            int i = profiles.size(); 
            profiles.add(new Profile(p));
            if(p.getCurrent()) {  
                pane.add(profiles.get(i).main, 11, 0, 1, GridPane.REMAINING); 
            }
            else { 
                pane.add(profiles.get(i).main, i, 11);
            } 
        }
        System.out.println("Profiles built succesfully");

        scene = new Scene(pane, 600, 600);
      
        stage.setScene(scene);
        stage.show();
    } 

    public static void main(String[] args) { 
        launch();
    }
}