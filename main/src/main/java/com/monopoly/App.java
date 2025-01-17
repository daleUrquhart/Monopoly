/**
 * GUI for monopoly application
 * @author Dale Urquhart 
 */

package com.monopoly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    /**
     * Start method for the Monopoly GUI, ground zero
     */
    @Override
    public void start(Stage stage) {
        Game game = new Game();
        System.out.println("Game built");
        GameView view = new GameView();
        System.out.println("GameView built");
        GameController controller = new GameController(game, view);
        System.out.println("GameController built");

        Scene scene = view.getScene();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        System.out.println("Starting game");
        try{
            controller.startGame(); 
        } catch(Exception e) {
            System.err.println("Error:\n"+e);
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
