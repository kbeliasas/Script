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
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

public class RuneCrafting {

    static Area AIR_ALTAR = new Area(2982, 3296, 2986, 3293);
    static Area MIND_ALTAR = new Area(2978, 3516, 2986, 3511);
    static Area BODY_ALTAR = new Area(3050, 3446, 3054, 3439);
    private static final int RUNES_PER_ESSENCE = 1;

    public static void craft() {

        if (!Inventory.contains(item ->
                item.getName().toLowerCase(Locale.ROOT).contains("essence"))) {
            Main.state = States.BANKING;
        }

        if (Main.state.equals(States.IDLE)) {
            Walking.walk(BODY_ALTAR.getRandomTile());
            var ruins = ruins();
            if (ruins != null && ruins.interact("Enter")) {
//                ruins.interact("Enter");
                Sleep.sleep(Calculations.random(1000, 2000));
                Main.state = States.RUNECRAFTING;
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
                            item.getName().toLowerCase(Locale.ROOT).contains("essence")) * RUNES_PER_ESSENCE;
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
                Sleep.sleep(Calculations.random(4000, 5000));
                Main.state = States.BANKING;
            }
        }
    }

    private static GameObject ruins() {
        return GameObjects.closest(altar -> altar.getName().toLowerCase(Locale.ROOT).contains("mysterious ruins")
        && altar.distance() < 8 && altar.canReach());
    }
}
