package com.monopoly;

public enum JailChoice {
    TRY_FOR_DOUBLES("Try for Doubles"),
    PAY_BAIL("Pay Bail"),
    USE_CARD("Use Card");

    private final String desc;

    JailChoice(String desc) { this.desc = desc;}

    public static JailChoice fromString(String desc) {
        if(USE_CARD.desc.equals(desc)) return USE_CARD;
        if(PAY_BAIL.desc.equals(desc)) return PAY_BAIL;
        if(TRY_FOR_DOUBLES.desc.equals(desc)) return TRY_FOR_DOUBLES;
        return null;
    }
}
