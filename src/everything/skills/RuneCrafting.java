package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.util.Locale;

public class RuneCrafting {

    static Area AIR_ALTAR = new Area(2982, 3296, 2986, 3293);
    static Area MIND_ALTAR = new Area(2978, 3516, 2986, 3511);
    static Area BODY_ALTAR = new Area(3050, 3446, 3054, 3439);

    public static void craft() {

        if (!Inventory.contains(item ->
                item.getName().toLowerCase(Locale.ROOT).contains("essence"))) {
            Main.state = States.BANKING;
        }

        if (Main.state.equals(States.IDLE)) {
            Walking.walk(BODY_ALTAR.getRandomTile());
            var ruins = GameObjects.closest("Mysterious ruins");
            if (ruins != null && ruins.canReach() && ruins.distance() < 10) {
                ruins.interact("Enter");
                Sleep.sleep(Calculations.random(1000, 2000));
                Main.state = States.RUNECRAFTING;
            } else {
                Logger.error("Something wrong ruins: " + ruins);
            }
        }

        if (Main.state.equals(States.BANKING)) {
            var portal = GameObjects.closest("Portal");

            if (portal != null && portal.canReach()) {
                Sleep.sleep(Calculations.random(600, 900));
                portal.interact("Use");
            } else {
                if (Banking.openBank()) {
                    var rune = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("rune"));
                    if (rune != null) {
                        var amount = Inventory.count(rune.getID());
                        Util.addLoot(rune.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    if (Bank.contains(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("essence"))) {
                        Bank.withdrawAll(item ->
                                item.getName().toLowerCase(Locale.ROOT).contains("essence"));
                    } else {
                        Main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.state = States.IDLE;
                    Main.goal = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("essence"));
                }
            }

        }

        if (Main.state.equals(States.RUNECRAFTING)) {
            var altar = GameObjects.closest("Altar");
            if (altar != null && altar.canReach()) {
                altar.interact("Craft-rune");
//                try {
//                    Thread.sleep(Calculations.random(1900, 2200));
//                } catch (InterruptedException e) {
//                    Logger.error("Interupted", e);
//                }
                Sleep.sleep(Calculations.random(5000, 6000));
                Main.state = States.BANKING;
            }
        }
    }
}
