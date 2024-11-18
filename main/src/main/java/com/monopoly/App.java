package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

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
        for(int i = 0; i < 11; i++) {
            for(int j = 0; j < 11; j++) {
                try {
                    InputStream inputStream = new FileInputStream("../resources/com/monopoly/"+i+"_"+j+".png");
                    Image image = new Image(inputStream);
                    ImageView imageView = new ImageView();  

                    imageView.setImage(image); 

                    if(i == 0 || i == 10) {
                        imageView.setFitWidth(i == j || Math.abs(i - j) == 10 ? 30 : 15);
                        imageView.setFitHeight(25);
                    }
                    else {
                        imageView.setFitHeight(i == j || Math.abs(i - j) == 10 ? 30 : 15);
                        imageView.setFitWidth(25);
                    } 
                    
                    pane.add(imageView, j, i);
                }
                catch(FileNotFoundException e) {
                    System.out.println("404:\t"+i+"_"+j+".png");
                    pane.add(new Label("404"), j, i);
                }
                catch(Exception e) {
                    System.out.println("\n\nWTF?!");
                    throw(e);
                }
            }
        } 
        pane.getChildren().add(boardViewer); 

        scene = new Scene(pane, 600, 600);
      
        stage.setScene(scene);
        stage.show();
    } 

    public static void main(String[] args) {
        launch();
    }
}