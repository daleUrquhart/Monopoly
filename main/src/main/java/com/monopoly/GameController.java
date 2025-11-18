/**
 * Game Controller, highest level
 */
package com.monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.monopoly.boardspaces.Go;
import com.monopoly.boardspaces.Property;
import com.monopoly.entities.Entity;
import com.monopoly.entities.Player;
import com.monopoly.events.AuctionEvent;
import com.monopoly.events.CompositeEvent;
import com.monopoly.events.JailTurnEvent;
import com.monopoly.events.MessageEvent;
import com.monopoly.events.RollEvent;

//import com.monopoly.events.PaymentEvent; 

/**
 * Master class connecting Game and GameView tasks
 * 
 * Responsible for: Prompting UI updates, 
 * Not responsible for: Handling game rules, using anything requiring JavaFX, 
 */
public class GameController {

    /**
     * Manages the game instance
     */
    private final GameModel game;

    /**
     * Manages the viewer instance
     */
    private final GameView view;
 
    /*
     * 
     */
    private final MessagePane mp;

    /**
     * 
     */
    private final PlayerBuilder pb;

    /**
     * Enabled once turn actions are all resolved
     */
    private Boolean rollEnabled;

    /**
     * Constructor for the controller
     * 
     * @param game Game instance
     * @param view Viewer instance
     */
    GameController(GameModel game, GameView view) {
        this.game = game;
        this.view = view;
        this.mp = view.getMessagePane();
        this.rollEnabled = true;
        this.pb = new PlayerBuilder(view.getMessagePane());
    }

    /**
     * Initialize the game and setup event handlers
     */
    void startGame() { 
        // Builds board components
        System.out.println("Placing tiles onto gameboard.");
        BoardBuilder bb = new BoardBuilder();  
        bb.buildTiles(game, view.getMainPane()); 
        view.setCenterPane(bb.buildCenter(game, view.getMainPane())); 
        
        // Builds and places dice grid onto center tile
        System.out.println("Placing dice onto center.");
        view.setDicePane(bb.buildDice(game, view.getMainPane())); 
        view.getDicePane().setOnMouseClicked(e -> {
            if(rollEnabled) {
                disableRoll();
                new RollEvent().execute(this);
            } else System.out.println("Attemped roll while dice disabled");
        });  
 
        // Builds barsPane
        System.out.println("Building bars pane.");
        view.setJailPane(bb.buildBars(game, view.getMainPane())); 
        view.getJailPane().setOnMouseClicked(e -> {
            if(rollEnabled) {
                disableRoll();
                new JailTurnEvent().execute(this);
            } else System.out.println("Attemped click while bars disabled");
        });  

        // Start player building process
        System.out.println("Beginging player building.");
        pb.initiatePlayerSetup();
        pb.loadPlayersToGame(game); 

        // Set a listener for when the player setup is complete
        pb.setPlayerSetupListener(() -> {
            pb.loadPlayersToGame(game);
            System.out.println("Players loaded, continuing game setup...");

            // Proceed with the game setup after players are loaded 
            view.getMessagePane().displayCurrent(game.getCurrentPlayer(), this); 
            view.showDice();
        });
    }  

    // Getters
    public GameView getView() {return view;} 
    public GameModel getModel() {return game;}

    // Roll controllers
    public void enableRoll() { rollEnabled = true; } 
    void disableRoll() { rollEnabled = false; } 

    void processNextTurn() {
        // Assign next player
        game.advanceTurn();  
        view.hidePrompt();

        /* 
        If next player is in jail, replace dice with jail bars.
        Once bars are clicked jail turn is handled.
        */
        if(game.getCurrentPlayer().inJail()) view.showJail(); 
        /*
        If player is not in jail, show dice.
        Once dice are clicked, roll is made and new tile is processed
        */
        else view.showDice();
    }

    public void showRollResults() {
        mp.clearMessages();
        Player current = game.getCurrentPlayer();
        RollResult res = game.rollDice(); 

        CompositeEvent event = new CompositeEvent();
        event.add(new MessageEvent("\nYou rolled a "+res.getRoll()+" and landed on "+res.getSpace().getName()));   

        // Handle Go passing
        if(res.passedGo()) event.add(Go.getInstance().onLand(current, game));

        // Handle Doubles logic 
        switch (res.getDoublesOutcome()) {
            case -1:
                event.add(new MessageEvent("\nYou rolled doubles, you get to roll again after your turn! "));
                break;
            case 1:
                event.add(new MessageEvent("\nThat was your third doubles, go to jail! "));
        }

        event.add(current.getLocation().onLand(current, game));
        
        event.execute(this);
        mp.displayCurrent(current, this);

        processNextTurn();
    }

    /**
     * Handles the turn of landing on an unwoned property
     */
    public void handleUnownedProperty() {  
        
        Player current = game.getCurrentPlayer();
        Property property = (Property) current.getLocation();

        // If player's networth exceeds property price show option to buy
        if(current.getNetWorth() >= property.getPrice()) { 
            mp.getBoolInput("\n"+property.getName()+" is not owned yet." +" Would you like to buy it?",
                    result -> {
                        // player chose to buy with sufficent cash
                        if(result) property.sellTo((Entity) current, property.getPrice()).execute(this);
                        // Player chose not to buy, property goes to auction
                        else {
                            mp.showMessage("\nYou chose not to buy this property, and it will be going up for auction. ");
                            new AuctionEvent(property, game.getPlayers()).execute(this);
                        }
                        mp.displayCurrent(current, this);
                    }
            );
        } 

        // The player can not afford the property, goes to auction
        else {
            mp.showMessage("\nYou can not afford this property, and it will be going up for auction. ");
            new AuctionEvent(property, game.getPlayers()).execute(this);
        }
    }
        
    public void promptJailOptions(boolean canPay, boolean hasCard) {
        mp.clear(); 
        List<String> options = new ArrayList<>();

        options.add("Try for Doubles");
        if (canPay) options.add("Pay Bail");
        if (hasCard) options.add("Use Card");

        mp.getChoiceInput(
            "You are in jail. Choose an option:",
            options,
            choice -> handleJailChoice(choice)
        );
    }

    private void handleJailChoice(String choice) { 
        JailOutcome result = game.processJailChoice(JailChoice.fromString(choice));
        mp.showMessage(result.toString()); 
        enableRoll();
        processNextTurn();
    }

    // UI updates regarding result of a jail turn
    public void showNoDoubles() {view.getMessagePane().showMessage("You failed to roll doubles ("+(getModel().getCurrentPlayer().getJailedTurns()+1)+"/3");}
    public void showDoubles() {view.getMessagePane().showMessage("You rolled doubles and are freed from jail");}
    public void showUsedJailCard() { view.getMessagePane().showMessage("Card used"); }
    public void showAutoPaidBail() { view.getMessagePane().showMessage("Bail paid"); }
    public void showAssetLiquidation() {view.getMessagePane().showMessage("You must liquidate assets in order to pay bail");}

    /**
     * Handles bankruptcy controlling 
     */
    public void handleBankruptcy(Entity bankrupter, Player bankrupted) {

        BankruptResult res = game.processBankruptcy(bankrupted, bankrupter);

        // If only one player left, declare teh winner
        if (res.getPlayersRemaining() == 1) {
            handleWinner();
            return;
        }

        // BANK bankrupts, auction all properties
        if (res.isAllAuctioned()) {
            res.getAuctionList().execute(this);
            return;
        }

        // PLAYER bankrupts, transfer properties and handle mortgage choices
        for (Property p : res.getMortgagedNeedingDecision()) {
            handleMortgagedPurchase((Player) bankrupter, p, ()->{});
        }
        res.transferred.execute(this);
    }

    /**
     * Handles game end state
     * Just deletes dice so nobody can go anymore to gracefully 'end game'
     */
    public void handleWinner() { 
        mp.clear(); 
        mp.showMessage(game.getCurrentPlayer().getName() + " wins the game!");
        mp.displayCurrent(game.getCurrentPlayer(), this);
        view.deletePrompts();
    }

    /**
     * Handles the purchasing of a mortgaged property. 
     * Returns the payment event due
     */
    void handleMortgagedPurchase(Player buyer, Property p, Runnable onComplete) {
        mp.getBoolInput(
            p.getName()+" is mortgaged. Unmortgage now or pay interest?",
            choice -> {
                if (choice) {
                    game.payMortgage(buyer, p).execute(this);
                } else {
                    game.payMortgageIntrest(buyer, p).execute(this);
                }
                onComplete.run();
            }
        );
    }


    /**
     * Handles a private sale action from the current player
     */
    void handlePrivateSale(Property property) {
        Player seller = (Player) property.getOwner(); 

        // Ask seller to select a buyer
        selectPlayer(seller, property, buyer -> {
            if (buyer == null) {
                mp.showMessage("No available players to sell this property to.");
                enableRoll();
                return;
            }

            // Ask seller to set asking price
            mp.getIntInput(
                "Private Sale",
                "Selling " + property.getName(),
                "Enter the asking price for " + buyer.getName() + ":",
                1, // minimum
                seller.getBalance(), // maximum 
                askingPrice -> {
                    if (askingPrice <= 0) {
                        mp.showMessage("Invalid price. Sale cancelled.");
                        enableRoll();
                        return;
                    }

                    // Prompt buyer to accept or reject
                    mp.getBoolInput( 
                        seller.getName() + " is offering to sell " + property.getName() +
                        " for $" + askingPrice + ".\n"+ buyer.getName() + ", do you accept this offer?",
                        accepted -> {
                            if (accepted) {
                                if (buyer.canAfford(askingPrice)) {
                                    buy(buyer, seller, property, askingPrice);
                                    mp.showMessage(
                                        buyer.getName() + " purchased " + property.getName() +
                                        " from " + seller.getName() + " for $" + askingPrice + "."
                                    );
                                } else {
                                    mp.showMessage(
                                        buyer.getName() + " cannot afford this offer."
                                    );
                                }
                            } else {
                                mp.showMessage(
                                    buyer.getName() + " declined the offer for " + property.getName() + "."
                                );
                            }
                            enableRoll();
                        }
                    );
                }
            );
        });
    }

    /**
     * Handles player selection of a private sale
     * Selected player gets the option to either accept the sale or deny
     * @param owner
     * @param location
     * @param callback
     */
    void selectPlayer(Player owner, Property location, Consumer<Player> callback) {
        List<Player> availablePlayers = new ArrayList<>();
        int minimum = location.isMortgaged() ? location.getMortgageValue() : 0;

        for (Player p : game.getPlayers()) {
            if (!p.equals(owner) && p.canAfford(minimum)) {
                availablePlayers.add(p);
            }
        }

        if (availablePlayers.isEmpty()) {
            view.getMessagePane().showMessage("No players can afford this property right now.");
            callback.accept(null);
            return;
        }

        // Build mapping of string representations to Player objects
        Map<String, Player> playerMap = new HashMap<>();
        List<String> playerNames = new ArrayList<>();
        for (Player p : availablePlayers) {
            String display = p.getName() + " ($" + p.getBalance() + ")";
            playerMap.put(display, p);
            playerNames.add(display);
        }

        // Use your existing getChoiceInput
        view.getMessagePane().getChoiceInput( 
            "Sell " + location.getName()+
            "\nChoose a player to sell to:",
            playerNames,
            selectedName -> {
                Player selectedPlayer = playerMap.get(selectedName);
                callback.accept(selectedPlayer);
            }
        );
    }  
    
    /**
     * Handles the purchase of a new proerty for the plaeyr bought by auction, or private sale
    * @param newProperty the property to buy
    * @param bid the amount the player bid for the property  
    */
    public void buy(Player buyer, Entity seller, Property p, int bid) {
        if (!p.isMortgaged()) {
            p.sellTo(buyer, bid).execute(this);
            return;
        }

        handleMortgagedPurchase(buyer, p, () -> {
            p.sellTo(buyer, bid).execute(this);
        });
    }
}