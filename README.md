# Monopoly-JAVAFX
Monopoly applicaiton launched by javafx
A continuation of the games series 

---

## Table of Contents
- [Description](#description) 
- [Features](#features) 
- [Installation](#installation) 
- [Requirements](#requirements)
- [Dev Notes](#devnotes)
- [bug Log](#buglog)
- [Execution Flow](#ExecutionFlow)

---

## Description
Monopoly game implemented with JavaFX
Played with several local users in a pass-and-play style flow
Full implementation of official Hasbro rules (auctioning, mortaging, etc.)

Initially partially developed and tabled for a year, coming back to this with a more matured skillset has been an interesting experience.
Sifting through the issues left at the last attempt and completing implementations of unfinished components emphasizes made me greatful for the good documentation practices.

Game board built with each tile being individually mapped onto a gridpans, pieces moved by having their position updated to the different tile instances.

Dice are mapped to the center of the center pane, updated to display different combinations based on the last roll made. 

Dice, tiles, and game pieces are iteratevly loaded.

---

## Features
- Custom player and piece selection
- Intuitive, and user friendly UI/UX
- Event driven 

---

## Installation
Instructions on how to install and run the project

```bash 
git clone https://github.com/daleUrquhart/Monopoly
cd Monopoly
mvn clean install 
mvn javafx:run
```

---

## Requirements
Maven
JavaFX 

---

## Screenshots

| Feature | Screenshot |
|---------|------------|
| Start Page | ![Main Menu](main/src/main/resources/screenshots/main_menu.png) |
| Chance Draw | ![Chance Draw](main/src/main/resources/screenshots/chance_draw.png) |
| Mid Game | ![Mid game](main/src/main/resources/screenshots/mid_game.png) |
| Buy Property | ![Buy property](main/src/main/resources/screenshots/buy_property.png) |
| Buy Property With Insufficent Funds | ![Buy Property With Insufficent Funds](main/src/main/resources/screenshots/broke_buy_property.png) |

---

## Devnotes 
Initialize dice in the Game init?  

Implement better UX (use side bar over popups for some events, etc.)

Make Go a singleton? Go, FP, other special tiles

Game flow UX improvements

---

## BugLog and implmentation notes
### 2 Player Tests

### Rolls enabled on Jail turn, bankrupting broken
- roll always works, but works when its not supposed to on jail tunrs
- Turns are not rotating, might be due to bankruptcey not working right (removes player from rotation, but doesnt remove them elsewheres??)
  - Balances can go into negetive....
- Once you own like 14 properties the display kinda breaks

### <Fied> Bad implementation of jail, dice not always re-enabled
- Complete overhaul of jail logic, similair to boolInput implementation, looks nice but doesnt enforce no rolling when not supposed to 
- After updating roll enabling logic dice no longer lock when not supposed to. Minor bug with jail (above)
- Dice work after landing on unowned proeprty adn a chance card that elts you buy a property, but that it
  - The chance card to a utility runs handleUnownedProperty, the only square method that lets a roll after so that checks out 
  - however, unowned, and other special squares do not work 
- Made adjustments to private sale
- Duct-taped a solution to phasing out reminaing GameView methods moved to message pane, could probably be made better by giving a message pane isntance to Player and Game with how much its used in there, but that raises coupling concerns
- Game crashes and behaves poorly on jail actions

### <Fixed> Illegal dice rolls allowed, poor display of properties after purchasing a new one
- Fix worked as expected
- Will also make the small fix of displaying a newly purchased property after submission of purchase before new roll by reloading player display after purchase
- Proposed solution works, dice only roll when expected now
- It is possible for dice rolls to be done during a turn action, look at fixing by disabling dice until it is ready


### <Fixed> Making message flow display more intuitive
Auction and property purchase done, required additions to Message pane and abstracting Auction logic. Alot of changes here will make this a sub-commit to solving this problem

Ok acks for CC and chance, etc. 
Yes and no buttons for landing on a property purchase prompt
We are clearing the pane in playerbuilding which is probably removing message and player displays from the thang

### <Fixed> Delegate message display logic into ists own API to clean up GameView
Addresssed, implemented new API, fixed property option logic aswell

Not giving an opportunity to manage assets after a player rolls. I want after a player rolls they get a chnce to manage assets after buying a new property and then the next player can manage theuir assets once the dice are rolled again

Maintaining the message pane properly has proven difficult, i feel like giving it its own class extending gridpane is the natural solution
Notes for implementation:
pane is the component that represents right side, it is a GridPane
GameView initialization adds pane to the right side of mainPane, will have to update pane creation to be the child of GridPane and give it a better name

(GameView) view.showMessage is hwo a message is currently displayed.
Maybe make right side message board its own object with a API for easily performning various recyclable displays liek purhcase aucction event etc.
Example:
MessageBoard view extends GridPane
view.clear()
view.addMessage(String message)
view.initiateAuction()
view.purchaseProperty(Property p)
etc.

Currently modified with GameView.showMessage
    /**
     * Adds a message ontop of currentPlayerDisplay
     * @param message Message to be displayed
     */
    void showMessage(String message) {
        pane.getChildren().clear();
        currentPlayerDisplay.getChildren().add(0, new Label(message));
        pane.add(currentPlayerDisplay, 0, 0); 
        
    }

### <Fixed> Player pieces not loading to respective corners
Both pieces loading to top left of tile (Seems to be just for Go tile on player creation)
Pieces overlap in first player's spot, likely done in Player constructor
Traces to setInitialLocation from Player constructor
Fixed by updating Player.ID before setting initial location, was not causing issues on later tiles as the ID was updated after the initial palcement so it knew where to properly go afterwarrds

#### Game crashes after player 2 goes for their first turn 
Happaned again: 2 landed on ones spot, on 2's first turn
Exception in thread "JavaFX Application Thread" java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
        at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        at java.base/java.util.Objects.checkIndex(Objects.java:365)
        at java.base/java.util.ArrayList.get(ArrayList.java:428)
        at com.monopoly/com.monopoly.Game.getNextPlayer(Game.java:189)
        at com.monopoly/com.monopoly.Game.handleRoll(Game.java:473)
        at com.monopoly/com.monopoly.GameController.lambda$startGame$0(GameController.java:54)

They were on the same tile here, maybe that was the issue, can go several turn cycles most times
Looks like players were lost and attempted to index the empty player list
        at java.base/java.util.ArrayList.get(ArrayList.java:428)
        at com.monopoly/com.monopoly.Game.getNextPlayer(Game.java:185)
        at com.monopoly/com.monopoly.Game.handleRoll(Game.java:466)
        at com.monopoly/com.monopoly.GameController.lambda$0(GameController.java:52)

#### <Fixed> First player went twice for after first round
After second turn, player 2 went 
This was probably just doubles after further testing, closed.

---

## ExecutionFlow
Build game
  Decks & game map

Build gameview

Build gamecontroller

GAME LOOP:
Game.handleRoll
Game.getNextPlayer
