/**
 * Dice creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import java.util.Random;

import com.monopoly.entities.Player;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Dice object class
 */
public final class Dice {

    
    /**
     * Path to resources directory
     */
    private static final String PATH = "/com/monopoly/";

    /**
     * Random connection
     */
    private final Random rand = new Random();

    /**
     * Stores roll 1 value
     */
    private int r1;

    /**
     * Stores roll 2 value
     */
    private int r2; 

    /**
     * Array containing different dice states
     */
    private Image[] dice;

    /**
     * Image for first die
     */
    private ImageView d1;

    /**
     * Image for second die
     */
    private ImageView d2;

    /**
     * Singleton dice instance
     */
    private static final Dice INSTANCE = new Dice();

    /**
     * Constructor for Dice
     */
    private Dice() {}

    public void attachUI(GridPane pane) {
        d1 = (ImageView) pane.getChildren().get(0);
        d2 = (ImageView) pane.getChildren().get(1);
        setDice();
    }

    /**
     * Gets the singleton dice instance
     * @return Dice instance for the game
     */
    public static Dice getInstance() {
        return INSTANCE;
    }

    /**
     * Assigns dice states to the dice array
     */
    void setDice() {
        dice = new Image[7];
        for(int i = 1; i < 7; i++) {
            try {
                dice[i-1] = new Image(getClass().getResourceAsStream(PATH + i + "_die.png"));
            } catch (Exception e) {
                System.err.println("Error building dice "+i+". Full message: \n"+e.toString());
            }
        }
    }
    /**
     * Roll simulator for dice
     * @return random int. between 2, and 12
     */
    int roll(Player p) {
        setD1(rand.nextInt(6)+1);
        setD2(rand.nextInt(6)+1);
        p.setRoll(getD1() + getD2());
        return getD1()+getD2();
    }

    /**
     * Gets the last roll of the dice
     */
    public int getRoll() {
        return getD1()+getD2();
    }

    /**
     * get roll from dice 1
     */
    int getD1() {
        return r1;
    }

    /**
     * get roll from dice 2
     */
    int getD2() {
        return r2;
    }

    /**
     * Set roll from dice 1
     */
    void setD1(int rolled) {
        r1 = rolled;
        d1.setImage(dice[rolled-1]);
    }

    /**
     * Set roll from dice 2
     */
    void setD2(int rolled) {
        r2 = rolled;
        d2.setImage(dice[rolled-1]);
    }

    /**
     * Returns whether or not the roll was doubles
     * @return true for if the roll was doubles
     */
    boolean doubles() {
        return getD1()==getD2();
    }
}