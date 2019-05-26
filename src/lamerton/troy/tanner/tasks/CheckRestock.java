package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import lamerton.troy.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class CheckRestock extends Task {

    private Main taskRunner;

    @Override
    public boolean validate() {
        return Main.restock && Main.checkRestock;
    }

    @Override
    public int execute() {
        Log.info("Restock check");
        if(Bank.isOpen()){
            Bank.close();
        }
        if(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()){
            closeGE();
        }

        boolean checkW = false;
        boolean checkD = false;
        boolean checkH = false;

        if (Rings.hasChargedRingW()) {
            checkW = true;
        }
        if (Rings.hasChargedRingD()) {
            checkD = true;
        }
        if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE + 1)) {
            checkH = true;
        }

        while (!Bank.isOpen()) {
            Bank.open();
            Time.sleep(1000);
        }
        if (!checkW && !Bank.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"))) {
            Main.newRingW = true;
            checkW = true;
        }
        if (!checkD && !Bank.contains(x -> x != null && x.getName().contains("dueling") && x.getName().matches(".*\\d+.*"))) {
            Main.newRingD = true;
            checkD = true;
        }
        if (!checkH && Bank.contains(Main.COWHIDE) || Bank.contains(Main.COWHIDE + 1)) {
            checkH = true;
        }
        if (!checkH || !checkD || !checkW) {
            Log.fine("Restocking");
            Main.restock = true;
        } else {
            Main.restock = false;
        }
        Main.checkRestock = false;
        return 1000;
    }

    private void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }

    }
}