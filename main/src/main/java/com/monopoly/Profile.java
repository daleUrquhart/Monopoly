package com.monopoly;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Player profile display, showing stats of a player in the game
 */
class Profile{
    
    Player player;

    VBox main;

    Boolean primary;

    Profile(Player p) {
        player = p;
        this.primary = p.getCurrent();
        Label name = new Label(p.getName());
        Label balance = new Label(String.valueOf(p.getBalance()));
        String propertiesContents = "";
        for(Property property : p.getProperties()) {
            propertiesContents+="\t"+property.toString()+"\n";
        }
        Label properties = new Label(propertiesContents);

        VBox box = new VBox(10, name, balance, properties);
        this.main = box;
    }
}
