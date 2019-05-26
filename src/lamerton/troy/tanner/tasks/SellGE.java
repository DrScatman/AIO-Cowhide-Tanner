package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.io.IOException;

public class SellGE extends Task {

    @Override
    public boolean validate() {
        return Main.restock && !Main.sold && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (!Main.checkedBank) {
            Banking.execute();
            Main.checkedBank = true;
        }

        Log.fine("Selling leathers");
        if (!GrandExchange.isOpen()) {
            Npcs.getNearest("Grand Exchange Clerk").interact("Exchange");
            Time.sleep(Main.randInt(700, 1300));
            return 1000;
        }

        if (Inventory.contains(Main.LEATHER_NOTE) && !Main.geSet) {
            GrandExchange.createOffer(RSGrandExchangeOffer.Type.SELL);
            Time.sleep(800);
            GrandExchangeSetup.setItem(Main.LEATHER_NOTE);
            Time.sleep(600);
            GrandExchangeSetup.setPrice(Main.leatherPrice);
            Time.sleep(600);
            GrandExchangeSetup.setQuantity(9999999);
            Time.sleep(600);
            GrandExchangeSetup.confirm();
            Time.sleep(600);
            if(GrandExchangeSetup.getItem() != null) {
                Main.geSet = true;
            }

            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            Keyboard.pressEnter();
        }

        if (GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));

            Main.sold = true;
            Main.checkedBank = false;
            Main.geSet = false;
            Log.info("Done selling");

            if (Main.restockMaxProfitHide) {
                Log.fine("Calculating most profitable hide...");
                try {
                    Main.setMaxProfitHide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (GrandExchange.getFirstActive() == null && !GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])){
            Main.sold = true;
            Main.checkedBank = false;
            Main.geSet = false;
            Log.info("Done selling 2");

            if (Main.calcMacProfitOnStart && Main.restockMaxProfitHide) {
                Log.fine("Calculating most profitable hide...");
                try {
                    Main.setMaxProfitHide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 1000;


        /*if (!ExGrandExchange.sell(Main.LEATHER_NOTE, 0, Main.leatherPrice, false)) {
            while (GrandExchange.getFirstActive().getProgress().equals(RSGrandExchangeOffer.Progress.IN_PROGRESS)) {
                Time.sleep(1000);
            }
            Time.sleep(1000);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(600, 1000));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(600, 1000));
            GrandExchange.collectAll();
            if (!Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
                Main.sold = true;
                Main.checkedBank = false;
                Log.info("Done selling 1");
            }
        } else {
            if (!Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
                Main.sold = true;
                Main.checkedBank = false;
                Log.info("Done selling 2");
            }
        }
        return 1000;*/
    }
}
