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
| Start Page | ![Main Menu](resources/screenshots/main_menu.png) |
| Chance Draw | ![Chance Draw](resources/screenshots/chance_draw.png) |
| Mid Game | ![Mid game](resources/screenshots/mid_game.png) |
| Buy Property | ![Buy property](resources/screenshots/buy_property.png) |
| Buy Property With Insufficent Funds | ![Buy Property With Insufficent Funds](resources/screenshots/broke_buy_property.png) |

---

## Devnotes
in Game.Game() line 79, chance and cc decks both pull from decks[0], that sounds bad

Initialize dice in the Game init? 

Fix Auction flow

Implement better UX (use side bar over popups for some events, etc.)

---

## BugLog
### 2 Player Tests
### Player pieces not loading to respective corners
Both pieces loading to top left of tile

#### Game crashes after player 2 goes for their first turn 
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
