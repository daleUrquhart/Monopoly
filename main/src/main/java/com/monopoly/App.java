package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 

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

    private static final double SCALE = 2.5;
    private static final int SQ = 33;
    private static final int MID = 24;

    private static Scene scene;

    private GridPane pane;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        System.out.println("START");
        pane = new GridPane(); 
 
        //Board
        InputStream board = new FileInputStream("../resources/com/monopoly/monopoly_board.png");
        Image boardImage = new Image(board);   
        ImageView boardViewer = new ImageView();   

        boardViewer.setImage(boardImage); 
        boardViewer.setFitWidth(530);
        boardViewer.setPreserveRatio(true); 
        
        //Iterative tile build    
        for(int col = 0; col < 11; col++) {
            for(int row = 0; row < 11; row++) {
                try {
                    InputStream inputStream = new FileInputStream("../resources/com/monopoly/"+col+"_"+row+".png");
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
                    InputStream inputStream = new FileInputStream("../resources/com/monopoly/"+10+"_"+10+".png");
                    Image image = new Image(inputStream);
                    ImageView imageView = new ImageView();
                    System.out.println("404:\t"+col+"_"+row+".png");

                    imageView.setImage(image);
                    
                    if(col == 0 || col == 10) {
                        if(row == 0 || row == 10) {
                            imageView.setFitHeight(SCALE * SQ);
                            imageView.setFitWidth(SCALE * SQ);
                            pane.add(imageView, col, row);
                        }
                        else {
                            imageView.setFitHeight(SCALE * MID);
                            imageView.setFitWidth(SCALE * SQ);
                            pane.add(imageView, col, row);
                        }                        
                    }
                    else if(row==0 || col==0 || row == 10 || col == 10) {
                        imageView.setFitHeight(SCALE * SQ);
                        imageView.setFitWidth(SCALE * MID);
                        pane.add(imageView, col, row);
                    }  
                }
                catch(Exception e) {
                    System.out.println("\n\nWTF?!");
                    throw(e);
                }
            }
        } 
        //pane.getChildren().add(boardViewer); 

        scene = new Scene(pane, 600, 600);
      
        stage.setScene(scene);
        stage.show();
    } 

    public static void main(String[] args) {
        launch();
    }
}