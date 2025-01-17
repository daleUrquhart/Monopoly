/**
 * Monopoly Game Module
 * 
 * This module serves as the core implementation of a Monopoly-style board game,
 * built using JavaFX for the user interface. It includes classes and logic for
 * managing the game board, player interactions, and game mechanics.
 * 
 * Dependencies:
 * - JavaFX modules for GUI elements
 * - Internal classes for game logic and data management
 * 
 * Exports:
 * - `com.monopoly` package containing all essential game components
 * 
 * @author Dale Urquhart
 * @since 2024-10-18
 */
module com.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens com.monopoly to javafx.fxml;
    exports com.monopoly;
}
