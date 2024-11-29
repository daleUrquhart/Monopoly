/**
 * Dice creation and function handler
 *
 * @author Dale Urquhart
 * @since 2024-10-18
 */

package com.monopoly;

import java.io.FileInputStream;
import java.io.FileNotFoundException; 
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Dice object class
 */
final class Dice {

    /**
     * Path to resources directory
     */
    private static final String PATH = "../resources/com/monopoly/";

    /**
     * Random connection
     */
    private final Random rand;

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
    private final ImageView d1;

    /**
     * Image for second die
     */
    private final ImageView d2;

    /**
     * Default constructor for Dice
     */
    Dice(GridPane d) {
        setDice();
        d1 = (ImageView) d.getChildren().get(0);
        d2 = (ImageView) d.getChildren().get(1);
        rand = new Random(); 
    }

    /**
     * Assigns dice states to the dice array
     */
    void setDice() {
        dice = new Image[7];
        for(int i = 1; i < 7; i++) {
            try {
                dice[i-1] = new Image(new FileInputStream(PATH + i + "_die.png"));
            } catch (FileNotFoundException e) {
                System.err.println("Image "+i+" note found. \n"+e.toString());
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