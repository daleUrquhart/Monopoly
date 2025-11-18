package com.monopoly;

public enum JailOutcome {
    USED_CARD("Jail card used."),
    PAID_BAIL("Bail paid."),
    NEEDS_LIQUIDATION("You must liquidate assets in order to pay bail."),
    BANKRUPT("You could not afford bail and went bankrupt"),
    FREED_BY_DOUBLES("You rolled doubles and were freed"),
    NO_DOUBLES("You fialed to roll doubles");

    private final String desc; 

    private JailOutcome(String desc) {
        this.desc = desc;
    }
    
    @Override public String toString() {return desc;}
} 
