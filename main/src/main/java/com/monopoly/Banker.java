/**
 * Rerpresents the banker, holds all banker related functions
 * @author Dale Urquhart
 * @since 2024-10-20
 */

 package com.monopoly;

 /**
  * Banker object that follows the Singleton pattern to ensure only one instance exists.
  */
 public final class Banker extends Entity {
 
    /**
     * Private static instance of the Banker class keeps the same banker easily accessable accross project  
     */ 
    private static final Banker INSTANCE = new Banker();

    /**
     * Private constructor to prevent instantiation from other classes
     */
    private Banker() {
        super("Banker", Integer.MAX_VALUE);
    }

    /**
     * Returns the single instance of the Banker class.
    * @return The singleton instance of Banker.
    */
    public static Banker getInstance() {
        return INSTANCE;
    }
}