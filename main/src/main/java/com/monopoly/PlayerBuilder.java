/**
 * PlayerBuilder
 * Handles the funcitonality of building players (Player count, names, game piece)
 */
package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
 
/**
 * Handles player building functionality
 */
public class PlayerBuilder {
    
    private static final int PIECE_SELECT_SIZE = 60;
    private static final int PIECE_DISPLAY_SIZE = 35;
    private static final String PATH = "../resources/com/monopoly/"; 

    private PlayerSetupListener listener;

    private Label label;
    private TextField field;
    private Button button;
    private HBox inputBox;
    private final GridPane pane;
    
    private int playerCount;
    private final ArrayList<String> takenNames;
    private final ArrayList<Image> availablePieces;
    private final ArrayList<ImageView> takenPieces;    
    

    /**
     * Constructor
     * @param game Game refrence
     * @param view GameView refrence
     */
    public PlayerBuilder(GridPane pane) {  
        this.pane = pane;
        
        takenNames = new ArrayList<>();
        availablePieces = buildPieces();
        takenPieces = new ArrayList<>();
    }

    /**
    * Initiates player building process
    */
    void initiatePlayerSetup() {
        buildPieces();
        buildPlayerCountUI(); 
    }

    /**
     * Loads in game pieces
     * @return ArrayList of Image refrences of game pieces
     */
    private ArrayList<Image> buildPieces() {
        ArrayList<Image> pieces = new ArrayList<>();
        try {
            for (int i = 0; i < 8; i++) {
                FileInputStream in = new FileInputStream(PATH + i + "_piece.png");
                pieces.add(new Image(in));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading game pieces: " + e.getMessage());
        }
        return pieces;
    }

    /**
     * Displays input for player count, waits for player to submit before proceeding
     * @param pane Display pane right of board
     */
    public void buildPlayerCountUI() {  
        label = new Label("Enter the number of players (2-4, press enter to submit): ");
        field = new TextField();
        button = new Button("Submit"); 
        inputBox = new HBox(10, label, field, button);

        field.setOnAction(e -> handlePlayerCountInput());
        button.setOnAction(e -> handlePlayerCountInput());
        pane.add(inputBox, 0, 0); 
    }

    /**
     * Validates entry for player count's input
     * @param pane Display pane right of board
     */
    private void handlePlayerCountInput() {
        try {
            int count = Integer.parseInt(field.getText());
            if (count < 2 || count > 4) {
                label.setText("Invalid input! Enter a number between 2 and 4.");
            } else {
                playerCount = count;
                pane.getChildren().remove(inputBox);
                
                buildNameInputUI();
            }
        } catch (NumberFormatException e) {
            label.setText("Please enter a valid integer.");
        }
    }

    /**
     * Displays input for recieving a player's name
     * @param pane Display pane right of board 
     */
    private void buildNameInputUI() {
        label = new Label("Enter a name for the player:");
        field = new TextField();
        button = new Button("Submit");
        inputBox = new HBox(10, label, field, button);

        field.setOnAction(e -> handleNameInput());
        button.setOnAction(e -> handleNameInput()); 
        pane.add(inputBox, 0, 0);
    } 

    /**
     * Validates entry for player count's input
     */
    private void handleNameInput() {
        String name = field.getText().trim();
        if (name.isEmpty() || takenNames.contains(name)) {
            label.setText("Invalid name! Try again.");
        } else {
            takenNames.add(name);
            pane.getChildren().remove(inputBox); 
            buildPieceSelectionUI(name);
        }
    }

    /**
     * Displays piece selection
     * @param pane Display pane right of board
     * @param playerName Player's name
     * @param playerIndex Current player submitting name
     */
    private void buildPieceSelectionUI(String playerName) {
        label = new Label("Select a piece for " + playerName + ":");
        VBox pieceBoxes = new VBox();
        HBox row1 = new HBox(10);
        HBox row2 = new HBox(10);

        for (int i = 0; i < availablePieces.size(); i++) {
            ImageView pieceView = new ImageView(availablePieces.get(i));
            PlayerBuilder.resizePiece(pieceView, PIECE_SELECT_SIZE);
            pieceView.setOnMouseClicked(e -> handlePieceSelection(pieceView));

            if (i < 4) {
                row1.getChildren().add(pieceView);
            } else {
                row2.getChildren().add(pieceView);
            }
        }

        pieceBoxes.getChildren().addAll(label, row1, row2);
        pane.add(pieceBoxes, 0, 1);
    }

    /**
     * Handles the piece selection 
     * @param pieceView Piece selected 
     */
    private void handlePieceSelection(ImageView pieceView) {
        availablePieces.remove(pieceView.getImage());
        PlayerBuilder.resizePiece(pieceView, PIECE_DISPLAY_SIZE);
        takenPieces.add(pieceView);
        pane.getChildren().clear();
        

        if (takenNames.size() < playerCount) {
            buildNameInputUI();
        } else {
            System.out.println("All players created: " + takenNames); 
            notifyPlayerSetupComplete();
        }
    }

    public void setPlayerSetupListener(PlayerSetupListener listener) {
        this.listener = listener;
    }

    private void notifyPlayerSetupComplete() {
        if (listener != null) {
            listener.onPlayerSetupComplete();
        }
    }

    /**
     * Sends player data to game
     * @param game Game refrence
     */
    void loadPlayersToGame(Game game) {
        game.setPlayerCount(playerCount);

        for(int i = 0; i < playerCount; i++) game.addPlayer(new Player(takenNames.get(i), game.getGo(), takenPieces.get(i)));
    }

    private static void resizePiece(ImageView piece, int SIZE) {
        piece.setFitHeight(SIZE);
        piece.setFitWidth(SIZE);
    }

    public List<String> getTakenNames() {
        return takenNames;
    }

    public List<ImageView> getSelectedPieces() {
        return takenPieces;
    }

    public int getPlayerCount() {
        return playerCount;
    } 
}
