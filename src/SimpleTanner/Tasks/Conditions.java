package SimpleTanner.Tasks;

import SimpleTanner.LeatherTanner;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;

class Conditions {
    // True if got anything other than coins or cowhide
    static boolean gotJunkOrLeather() {
        return Inventory.contains(
            item -> item != null
                && !item.getName().equals("Coins")
                && item.getId() != LeatherTanner.COWHIDE
        );
    }

    static boolean gotCowhide() {
        return Inventory.contains(LeatherTanner.COWHIDE);
    }

    static boolean gotEnoughCoins() {
        Item coins = Inventory.getFirst("Coins");
        return coins != null && coins.getStackSize() >= 27;
    }

    static boolean nearTanner() {
        return LeatherTanner.TANNER_AREA.getCenter().distance(Players.getLocal()) < 10;
    }

    static boolean atBank() {
        return BankLocation.AL_KHARID.getPosition().distance(Players.getLocal().getPosition()) < 3;
    }
}
