package com.monopoly.events;

import com.monopoly.Banker;
import com.monopoly.GameController;
import com.monopoly.Player;

public final class PaymentPerDevelopmentEvent extends GameEvent {
    private final int costPerHouse;
    private final int costPerHotel;

    public PaymentPerDevelopmentEvent(int costPerHouse, int costPerHotel) {
        this.costPerHouse = costPerHouse;
        this.costPerHotel = costPerHotel;
    }

    @Override public void execute(GameController controller) {
        Player p = controller.getModel().getCurrentPlayer();
        int houses = p.getTotalHouses();
        int hotels = p.getTotalHotels();
        int total = houses * costPerHouse + hotels * costPerHotel;

        p.pay(Banker.getInstance(), total);
    }
}
