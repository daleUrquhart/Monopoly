package com.monopoly.events;
 
import com.monopoly.GameController;
import com.monopoly.JailOutcome;
import com.monopoly.entities.Banker;

public final class MaxJailTurnsEvent extends GameEvent {
 
    @Override public void execute(GameController controller) {
        JailOutcome result = controller.getModel().processMaxJailTurns();

        switch (result) {
            case NO_DOUBLES: controller.showNoDoubles();
            case FREED_BY_DOUBLES: controller.showDoubles();
            case USED_CARD: controller.showUsedJailCard();
            case PAID_BAIL: controller.showAutoPaidBail();
            case NEEDS_LIQUIDATION: controller.showAssetLiquidation();
            case BANKRUPT: controller.handleBankruptcy(Banker.getInstance(), controller.getModel().getCurrentPlayer());
        } 
    }
}
