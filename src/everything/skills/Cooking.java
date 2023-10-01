package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

public class Cooking {

    private static final Area FALADOR_COOKING = new Area(3037, 3364, 3040, 3362);
    private static final Area EDGEVILLE_COOKING = new Area(3077, 3495, 3080, 3489);
    private static final String FISH_RAW = "raw lobster";
    private static final String FISH = "lobster";

    public static void cook() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains(FISH_RAW))) {
                Main.state = States.BANKING;
                Inventory.dropAll(item -> item.getName().toLowerCase(Locale.ROOT).contains("burnt"));
            }

            if (Main.state.equals(States.BANKING)) {
                if (Banking.openBank()) {
                    var fish = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH));
                    if (fish != null) {
                        var amount = Inventory.count(fish.getID());
                        Util.addLoot(fish.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    if (Bank.contains(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH_RAW))) {
                        Bank.withdrawAll(item ->
                                item.getName().toLowerCase(Locale.ROOT).contains(FISH_RAW));
                    } else {
                        Main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.state = States.IDLE;
                    Main.goal = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH_RAW));
                }
            }

            if (!Main.state.equals(States.BANKING)) {
                var cookingRange = getCookingRange();
                if (cookingRange != null) {
                    cookingRange.interact("Cook");
                    Sleep.sleep(Calculations.random(1500, 1800));
                    Keyboard.typeSpecialKey(32);
                    Main.state = States.COOKING;
                } else {
                    Walking.walk(EDGEVILLE_COOKING.getRandomTile());
                }
            }
        }

    }

    private static GameObject getCookingRange() {
        return GameObjects.closest(range ->
                range.getName().toLowerCase(Locale.ROOT).contains("stove") &&
                        range.canReach() && range.distance() <= 10);
    }
}
